package com.skylark.service;

import com.skylark.dto.AccountVO;
import com.skylark.dto.BillingRecordVO;
import com.skylark.entity.BillingRecordEntity;
import com.skylark.entity.PiAccountEntity;
import com.skylark.repository.BillingRecordRepository;
import com.skylark.repository.PiAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccountService {

    @Autowired
    private PiAccountRepository piAccountRepository;

    @Autowired
    private BillingRecordRepository billingRecordRepository;

    public AccountVO getAccountByGroupName(String groupName) {
        PiAccountEntity account = piAccountRepository.findByGroupName(groupName).orElse(null);
        if (account == null) {
            return null;
        }
        return toAccountVO(account);
    }

    public AccountVO getAccountById(Long id) {
        PiAccountEntity account = piAccountRepository.findById(id).orElse(null);
        if (account == null) {
            return null;
        }
        return toAccountVO(account);
    }

    public List<AccountVO> getAllAccounts() {
        return piAccountRepository.findAll().stream()
                .map(this::toAccountVO)
                .collect(Collectors.toList());
    }

    public List<BillingRecordVO> getBillingRecordsByAccount(Long piAccountId) {
        return billingRecordRepository.findByPiAccountIdOrderByCreatedAtDesc(piAccountId).stream()
                .map(this::toBillingRecordVO)
                .collect(Collectors.toList());
    }

    private AccountVO toAccountVO(PiAccountEntity entity) {
        AccountVO vo = new AccountVO();
        vo.setId(entity.getId());
        vo.setPiName(entity.getPiName());
        vo.setGroupName(entity.getGroupName());
        vo.setBalance(entity.getBalance());
        return vo;
    }

    private BillingRecordVO toBillingRecordVO(BillingRecordEntity entity) {
        BillingRecordVO vo = new BillingRecordVO();
        vo.setId(entity.getId());
        vo.setAppointmentId(entity.getAppointmentId());
        vo.setPiAccountId(entity.getPiAccountId());
        vo.setAmount(entity.getAmount());
        vo.setType(entity.getType());
        vo.setDescription(entity.getDescription());
        vo.setCreatedAt(entity.getCreatedAt());
        return vo;
    }
}
