package com.skylark.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/health")
public class HealthController {

    @GetMapping
    public Map<String, String> healthCheck() {
        Map<String, String> status = new HashMap<>();
        status.put("status", "UP");
        status.put("message", "Sky-Lark 后端服务运行中");
        return status;
    }
}
