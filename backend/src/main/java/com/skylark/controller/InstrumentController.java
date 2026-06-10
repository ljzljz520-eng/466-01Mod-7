package com.skylark.controller;

import com.skylark.entity.InstrumentEntity;
import com.skylark.service.InstrumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/instruments")
public class InstrumentController {

    @Autowired
    private InstrumentService instrumentService;

    @GetMapping
    public List<InstrumentEntity> getAll() {
        return instrumentService.getAllInstruments();
    }

    @GetMapping("/available")
    public List<InstrumentEntity> getAvailable() {
        return instrumentService.getAvailableInstruments();
    }

    @GetMapping("/{id}")
    public InstrumentEntity getById(@PathVariable Long id) {
        return instrumentService.getInstrumentById(id);
    }
}
