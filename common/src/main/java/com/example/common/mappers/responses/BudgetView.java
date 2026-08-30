package com.example.common.mappers.responses;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BudgetView extends AuditableView {

    private BigDecimal monthlyLimit;
    private Integer month;
    private Integer year;

    private Long userId;
    private Long categoryId;
}