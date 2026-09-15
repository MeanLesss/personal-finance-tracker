package com.example.common.mappers.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MonthlyTrendItem {

    private Integer month;
    private String monthName;
    private BigDecimal income;
    private BigDecimal expense;
    private BigDecimal netSavings;
    private Double savingsRate;
    private Integer transactionCount;

}
