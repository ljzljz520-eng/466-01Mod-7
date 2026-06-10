package com.skylark.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CostEstimateDTO {

    private BigDecimal timeCost;

    private BigDecimal consumableCost;

    private BigDecimal urgentSurcharge;

    private BigDecimal totalEstimatedCost;

    private BigDecimal currentBalance;

    private Boolean balanceSufficient;
}
