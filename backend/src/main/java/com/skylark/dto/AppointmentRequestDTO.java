package com.skylark.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AppointmentRequestDTO {

    private Long instrumentId;

    private Long piAccountId;

    private String userName;

    private String userRole;

    private BigDecimal bookedHours;

    private Boolean useConsumables;

    private Boolean isUrgent;
}
