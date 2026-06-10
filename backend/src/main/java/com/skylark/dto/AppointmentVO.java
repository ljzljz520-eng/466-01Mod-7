package com.skylark.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AppointmentVO {

    private Long id;

    private Long instrumentId;

    private String instrumentName;

    private String instrumentType;

    private Long piAccountId;

    private String piName;

    private String groupName;

    private String userName;

    private String userRole;

    private BigDecimal bookedHours;

    private Boolean useConsumables;

    private Boolean isUrgent;

    private BigDecimal estimatedCost;

    private BigDecimal actualHours;

    private BigDecimal actualCost;

    private String status;

    private BigDecimal adjustmentAmount;

    private String adjustmentReason;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
