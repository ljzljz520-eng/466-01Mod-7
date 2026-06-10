package com.skylark.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "appointment")
public class AppointmentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long instrumentId;

    @Column(nullable = false)
    private Long piAccountId;

    @Column(nullable = false)
    private String userName;

    @Column(nullable = false, length = 50)
    private String userRole;

    @Column(nullable = false, precision = 5, scale = 1)
    private BigDecimal bookedHours;

    @Column(nullable = false)
    private Boolean useConsumables;

    @Column(nullable = false)
    private Boolean isUrgent;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal estimatedCost;

    @Column(precision = 5, scale = 1)
    private BigDecimal actualHours;

    @Column(precision = 10, scale = 2)
    private BigDecimal actualCost;

    @Column(nullable = false, length = 50)
    private String status;

    @Column(precision = 10, scale = 2)
    private BigDecimal adjustmentAmount;

    private String adjustmentReason;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
