package com.skylark.repository;

import com.skylark.entity.BillingRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BillingRecordRepository extends JpaRepository<BillingRecordEntity, Long> {

    List<BillingRecordEntity> findByPiAccountIdOrderByCreatedAtDesc(Long piAccountId);

    List<BillingRecordEntity> findByAppointmentId(Long appointmentId);
}
