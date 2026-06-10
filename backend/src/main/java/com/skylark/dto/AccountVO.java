package com.skylark.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AccountVO {

    private Long id;

    private String piName;

    private String groupName;

    private BigDecimal balance;
}
