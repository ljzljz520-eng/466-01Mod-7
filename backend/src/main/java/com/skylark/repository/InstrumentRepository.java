package com.skylark.repository;

import com.skylark.entity.InstrumentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InstrumentRepository extends JpaRepository<InstrumentEntity, Long> {

    List<InstrumentEntity> findByStatus(String status);

    List<InstrumentEntity> findByType(String type);
}
