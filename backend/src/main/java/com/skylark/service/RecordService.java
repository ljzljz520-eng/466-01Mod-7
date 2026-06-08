package com.skylark.service;

import com.skylark.entity.RecordEntity;
import com.skylark.repository.RecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecordService {

    @Autowired
    private RecordRepository recordRepository;

    public List<RecordEntity> getAll() {
        return recordRepository.findAll();
    }

    public RecordEntity save(RecordEntity recordEntity) {
        return recordRepository.save(recordEntity);
    }
}
