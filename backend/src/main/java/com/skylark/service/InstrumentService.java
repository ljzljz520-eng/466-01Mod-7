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
public class InstrumentService {

    @Autowired
    private InstrumentRepository instrumentRepository;

    public List<InstrumentEntity> getAllInstruments() {
        return instrumentRepository.findAll();
    }

    public List<InstrumentEntity> getAvailableInstruments() {
        return instrumentRepository.findByStatus("AVAILABLE");
    }

    public InstrumentEntity getInstrumentById(Long id) {
        return instrumentRepository.findById(id).orElse(null);
    }
}
