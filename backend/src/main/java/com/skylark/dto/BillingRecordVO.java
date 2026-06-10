package com.skylark.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class BillingRecordVO {

    private Long id;

    private Long appointmentId;

    private Long piAccountId;

    private BigDecimal amount;

    private String type;

    private String description;

    private LocalDateTime createdAt;
}
