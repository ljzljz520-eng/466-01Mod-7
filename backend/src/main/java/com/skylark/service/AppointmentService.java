package com.skylark.service;

import com.skylark.dto.*;
import com.skylark.entity.*;
import com.skylark.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private InstrumentRepository instrumentRepository;

    @Autowired
    private PiAccountRepository piAccountRepository;

    @Autowired
    private BillingRecordRepository billingRecordRepository;

    public CostEstimateDTO estimateCost(AppointmentRequestDTO request) {
        InstrumentEntity instrument = instrumentRepository.findById(request.getInstrumentId())
                .orElseThrow(() -> new RuntimeException("仪器不存在"));

        PiAccountEntity account = piAccountRepository.findById(request.getPiAccountId())
                .orElseThrow(() -> new RuntimeException("课题组账户不存在"));

        BigDecimal timeCost = instrument.getRatePerHour().multiply(request.getBookedHours());
        BigDecimal consumableCost = Boolean.TRUE.equals(request.getUseConsumables())
                ? instrument.getConsumableFee() : BigDecimal.ZERO;
        BigDecimal base = timeCost.add(consumableCost);
        BigDecimal urgentSurcharge = Boolean.TRUE.equals(request.getIsUrgent())
                ? base.multiply(instrument.getUrgentSurchargeRate()) : BigDecimal.ZERO;

        BigDecimal total = timeCost.add(consumableCost).add(urgentSurcharge).setScale(2, RoundingMode.HALF_UP);

        CostEstimateDTO dto = new CostEstimateDTO();
        dto.setTimeCost(timeCost.setScale(2, RoundingMode.HALF_UP));
        dto.setConsumableCost(consumableCost.setScale(2, RoundingMode.HALF_UP));
        dto.setUrgentSurcharge(urgentSurcharge.setScale(2, RoundingMode.HALF_UP));
        dto.setTotalEstimatedCost(total);
        dto.setCurrentBalance(account.getBalance());
        dto.setBalanceSufficient(account.getBalance().compareTo(total) >= 0);
        return dto;
    }

    @Transactional
    public AppointmentVO createAppointment(AppointmentRequestDTO request) {
        CostEstimateDTO estimate = estimateCost(request);
        if (!estimate.getBalanceSufficient()) {
            throw new RuntimeException("课题组账户余额不足，无法提交预约。当前余额: "
                    + estimate.getCurrentBalance() + "，预估费用: " + estimate.getTotalEstimatedCost());
        }

        InstrumentEntity instrument = instrumentRepository.findById(request.getInstrumentId())
                .orElseThrow(() -> new RuntimeException("仪器不存在"));
        if (!"AVAILABLE".equals(instrument.getStatus())) {
            throw new RuntimeException("该仪器当前不可预约");
        }

        PiAccountEntity account = piAccountRepository.findById(request.getPiAccountId())
                .orElseThrow(() -> new RuntimeException("课题组账户不存在"));

        account.setBalance(account.getBalance().subtract(estimate.getTotalEstimatedCost()));
        account.setUpdatedAt(LocalDateTime.now());
        piAccountRepository.save(account);

        AppointmentEntity entity = new AppointmentEntity();
        entity.setInstrumentId(request.getInstrumentId());
        entity.setPiAccountId(request.getPiAccountId());
        entity.setUserName(request.getUserName());
        entity.setUserRole(request.getUserRole());
        entity.setBookedHours(request.getBookedHours());
        entity.setUseConsumables(request.getUseConsumables());
        entity.setIsUrgent(request.getIsUrgent());
        entity.setEstimatedCost(estimate.getTotalEstimatedCost());
        entity.setStatus("CONFIRMED");
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        AppointmentEntity saved = appointmentRepository.save(entity);

        BillingRecordEntity billing = new BillingRecordEntity();
        billing.setAppointmentId(saved.getId());
        billing.setPiAccountId(request.getPiAccountId());
        billing.setAmount(estimate.getTotalEstimatedCost().negate());
        billing.setType("PREPAY");
        billing.setDescription("预约预扣费 - " + instrument.getName());
        billing.setCreatedAt(LocalDateTime.now());
        billingRecordRepository.save(billing);

        return toAppointmentVO(saved);
    }

    @Transactional
    public AppointmentVO settleAppointment(SettlementRequestDTO request) {
        AppointmentEntity appointment = appointmentRepository.findById(request.getAppointmentId())
                .orElseThrow(() -> new RuntimeException("预约记录不存在"));

        if (!"COMPLETED".equals(appointment.getStatus()) && !"CONFIRMED".equals(appointment.getStatus())) {
            throw new RuntimeException("当前预约状态不允许结算");
        }

        InstrumentEntity instrument = instrumentRepository.findById(appointment.getInstrumentId())
                .orElseThrow(() -> new RuntimeException("仪器不存在"));

        PiAccountEntity account = piAccountRepository.findById(appointment.getPiAccountId())
                .orElseThrow(() -> new RuntimeException("课题组账户不存在"));

        BigDecimal actualTimeCost = instrument.getRatePerHour().multiply(request.getActualHours());
        BigDecimal consumableCost = Boolean.TRUE.equals(appointment.getUseConsumables())
                ? instrument.getConsumableFee() : BigDecimal.ZERO;
        BigDecimal base = actualTimeCost.add(consumableCost);
        BigDecimal urgentSurcharge = Boolean.TRUE.equals(appointment.getIsUrgent())
                ? base.multiply(instrument.getUrgentSurchargeRate()) : BigDecimal.ZERO;
        BigDecimal actualCost = actualTimeCost.add(consumableCost).add(urgentSurcharge).setScale(2, RoundingMode.HALF_UP);

        BigDecimal difference = actualCost.subtract(appointment.getEstimatedCost());

        appointment.setActualHours(request.getActualHours());
        appointment.setActualCost(actualCost);
        appointment.setAdjustmentAmount(difference);
        appointment.setAdjustmentReason(request.getAdjustmentReason());
        appointment.setStatus("SETTLED");
        appointment.setUpdatedAt(LocalDateTime.now());
        appointmentRepository.save(appointment);

        if (difference.compareTo(BigDecimal.ZERO) != 0) {
            BigDecimal adjustmentAmount = difference.negate();
            account.setBalance(account.getBalance().subtract(difference));
            account.setUpdatedAt(LocalDateTime.now());
            piAccountRepository.save(account);

            String adjustType = difference.compareTo(BigDecimal.ZERO) > 0 ? "SUPPLEMENT" : "REFUND";
            BillingRecordEntity adjustBilling = new BillingRecordEntity();
            adjustBilling.setAppointmentId(appointment.getId());
            adjustBilling.setPiAccountId(appointment.getPiAccountId());
            adjustBilling.setAmount(adjustmentAmount);
            adjustBilling.setType(adjustType);
            String direction = difference.compareTo(BigDecimal.ZERO) > 0 ? "补扣" : "退还";
            adjustBilling.setDescription(direction + "差额 " + difference.abs() + " 元 - "
                    + (request.getAdjustmentReason() != null ? request.getAdjustmentReason() : "实际使用结算"));
            adjustBilling.setCreatedAt(LocalDateTime.now());
            billingRecordRepository.save(adjustBilling);
        }

        return toAppointmentVO(appointment);
    }

    @Transactional
    public AppointmentVO completeAppointment(Long appointmentId) {
        AppointmentEntity appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("预约记录不存在"));
        if (!"CONFIRMED".equals(appointment.getStatus())) {
            throw new RuntimeException("只有已确认的预约才能标记完成");
        }
        appointment.setStatus("COMPLETED");
        appointment.setUpdatedAt(LocalDateTime.now());
        appointmentRepository.save(appointment);
        return toAppointmentVO(appointment);
    }

    @Transactional
    public AppointmentVO cancelAppointment(Long appointmentId) {
        AppointmentEntity appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("预约记录不存在"));
        if ("SETTLED".equals(appointment.getStatus()) || "CANCELLED".equals(appointment.getStatus())) {
            throw new RuntimeException("当前预约状态不允许取消");
        }

        PiAccountEntity account = piAccountRepository.findById(appointment.getPiAccountId())
                .orElseThrow(() -> new RuntimeException("课题组账户不存在"));

        account.setBalance(account.getBalance().add(appointment.getEstimatedCost()));
        account.setUpdatedAt(LocalDateTime.now());
        piAccountRepository.save(account);

        BillingRecordEntity refund = new BillingRecordEntity();
        refund.setAppointmentId(appointment.getId());
        refund.setPiAccountId(appointment.getPiAccountId());
        refund.setAmount(appointment.getEstimatedCost());
        refund.setType("REFUND");
        refund.setDescription("取消预约 - 退还预扣费用");
        refund.setCreatedAt(LocalDateTime.now());
        billingRecordRepository.save(refund);

        appointment.setStatus("CANCELLED");
        appointment.setUpdatedAt(LocalDateTime.now());
        appointmentRepository.save(appointment);

        return toAppointmentVO(appointment);
    }

    public List<AppointmentVO> getAppointmentsByUser(String userName) {
        return appointmentRepository.findByUserName(userName).stream()
                .map(this::toAppointmentVO)
                .collect(Collectors.toList());
    }

    public List<AppointmentVO> getAppointmentsByGroup(Long piAccountId) {
        return appointmentRepository.findByPiAccountId(piAccountId).stream()
                .map(this::toAppointmentVO)
                .collect(Collectors.toList());
    }

    public List<AppointmentVO> getAppointmentsByStatus(String status) {
        return appointmentRepository.findByStatus(status).stream()
                .map(this::toAppointmentVO)
                .collect(Collectors.toList());
    }

    public AppointmentVO getAppointmentById(Long id) {
        return appointmentRepository.findById(id)
                .map(this::toAppointmentVO)
                .orElse(null);
    }

    public List<AppointmentVO> getAllAppointments() {
        return appointmentRepository.findAll().stream()
                .map(this::toAppointmentVO)
                .collect(Collectors.toList());
    }

    private AppointmentVO toAppointmentVO(AppointmentEntity entity) {
        AppointmentVO vo = new AppointmentVO();
        vo.setId(entity.getId());
        vo.setInstrumentId(entity.getInstrumentId());
        vo.setPiAccountId(entity.getPiAccountId());
        vo.setUserName(entity.getUserName());
        vo.setUserRole(entity.getUserRole());
        vo.setBookedHours(entity.getBookedHours());
        vo.setUseConsumables(entity.getUseConsumables());
        vo.setIsUrgent(entity.getIsUrgent());
        vo.setEstimatedCost(entity.getEstimatedCost());
        vo.setActualHours(entity.getActualHours());
        vo.setActualCost(entity.getActualCost());
        vo.setStatus(entity.getStatus());
        vo.setAdjustmentAmount(entity.getAdjustmentAmount());
        vo.setAdjustmentReason(entity.getAdjustmentReason());
        vo.setCreatedAt(entity.getCreatedAt());
        vo.setUpdatedAt(entity.getUpdatedAt());

        instrumentRepository.findById(entity.getInstrumentId()).ifPresent(inst -> {
            vo.setInstrumentName(inst.getName());
            vo.setInstrumentType(inst.getType());
        });

        piAccountRepository.findById(entity.getPiAccountId()).ifPresent(acc -> {
            vo.setPiName(acc.getPiName());
            vo.setGroupName(acc.getGroupName());
        });

        return vo;
    }
}
