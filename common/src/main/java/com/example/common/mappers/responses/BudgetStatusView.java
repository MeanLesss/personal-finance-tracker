package com.example.common.mappers.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BudgetStatusView {

    private Long budgetId;
    private Long categoryId;
    private String categoryName;
    private Integer month;
    private Integer year;
    private BigDecimal monthlyLimit;
    private BigDecimal actualSpend;
    private BigDecimal remaining;
    private Double percentageUsed;
    private Boolean isOverBudget;

}
