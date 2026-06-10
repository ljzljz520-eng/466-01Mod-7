package com.skylark.repository;

import com.skylark.entity.PiAccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PiAccountRepository extends JpaRepository<PiAccountEntity, Long> {

    Optional<PiAccountEntity> findByGroupName(String groupName);

    List<PiAccountEntity> findByPiName(String piName);
}
