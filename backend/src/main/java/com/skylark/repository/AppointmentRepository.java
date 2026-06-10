package com.skylark.repository;

import com.skylark.entity.AppointmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<AppointmentEntity, Long> {

    List<AppointmentEntity> findByPiAccountId(Long piAccountId);

    List<AppointmentEntity> findByUserName(String userName);

    List<AppointmentEntity> findByStatus(String status);

    List<AppointmentEntity> findByPiAccountIdAndStatus(Long piAccountId, String status);
}
