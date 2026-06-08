package com.skylark.controller;

import com.skylark.entity.RecordEntity;
import com.skylark.service.RecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/records")
public class RecordController {

    @Autowired
    private RecordService recordService;

    @GetMapping
    public List<RecordEntity> getAll() {
        return recordService.getAll();
    }

    @PostMapping
    public RecordEntity create(@RequestBody RecordEntity recordEntity) {
        return recordService.save(recordEntity);
    }
}
