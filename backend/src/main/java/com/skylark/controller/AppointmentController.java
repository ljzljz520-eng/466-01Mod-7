package com.skylark.controller;

import com.skylark.dto.*;
import com.skylark.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @PostMapping("/estimate")
    public CostEstimateDTO estimateCost(@RequestBody AppointmentRequestDTO request) {
        return appointmentService.estimateCost(request);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AppointmentVO createAppointment(@RequestBody AppointmentRequestDTO request) {
        return appointmentService.createAppointment(request);
    }

    @PostMapping("/settle")
    public AppointmentVO settleAppointment(@RequestBody SettlementRequestDTO request) {
        return appointmentService.settleAppointment(request);
    }

    @PutMapping("/{id}/complete")
    public AppointmentVO completeAppointment(@PathVariable Long id) {
        return appointmentService.completeAppointment(id);
    }

    @PutMapping("/{id}/cancel")
    public AppointmentVO cancelAppointment(@PathVariable Long id) {
        return appointmentService.cancelAppointment(id);
    }

    @GetMapping
    public List<AppointmentVO> getAll() {
        return appointmentService.getAllAppointments();
    }

    @GetMapping("/{id}")
    public AppointmentVO getById(@PathVariable Long id) {
        return appointmentService.getAppointmentById(id);
    }

    @GetMapping("/user/{userName}")
    public List<AppointmentVO> getByUser(@PathVariable String userName) {
        return appointmentService.getAppointmentsByUser(userName);
    }

    @GetMapping("/group/{piAccountId}")
    public List<AppointmentVO> getByGroup(@PathVariable Long piAccountId) {
        return appointmentService.getAppointmentsByGroup(piAccountId);
    }

    @GetMapping("/status/{status}")
    public List<AppointmentVO> getByStatus(@PathVariable String status) {
        return appointmentService.getAppointmentsByStatus(status);
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleRuntimeEx(RuntimeException ex) {
        return Map.of("message", ex.getMessage());
    }
}
