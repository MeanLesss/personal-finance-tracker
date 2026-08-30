package com.example.common.payload.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BudgetRequest {

    private BigDecimal monthlyLimit;
    private Integer month;
    private Integer year;

    private Long userId;
    private Long categoryId;
}