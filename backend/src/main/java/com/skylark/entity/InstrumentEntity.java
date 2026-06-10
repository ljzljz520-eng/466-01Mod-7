package com.skylark.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "instrument")
public class InstrumentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal ratePerHour;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal consumableFee;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal urgentSurchargeRate;

    @Column(nullable = false, length = 50)
    private String status;

    private String description;
}
