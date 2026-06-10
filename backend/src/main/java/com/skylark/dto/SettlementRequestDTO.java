package com.skylark.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SettlementRequestDTO {

    private Long appointmentId;

    private BigDecimal actualHours;

    private String adjustmentReason;
}
