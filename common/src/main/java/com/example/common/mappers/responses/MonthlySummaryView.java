package com.example.common.mappers.responses;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MonthlySummaryView {

    private Integer month;
    private String monthName;
    private Integer year;
    private LocalDate startDate;
    private LocalDate endDate;

    private Long userId;
    private Long accountId;
    private String accountName;
    private String currency;

    private BigDecimal totalIncome;
    private BigDecimal totalExpense;
    private BigDecimal netSavings;
    private Double savingsRate;

    private Integer totalTransactions;
    private Integer incomeTransactions;
    private Integer expenseTransactions;

    public MonthlySummaryView(Long userId, Integer month, Integer year, BigDecimal totalIncome, BigDecimal totalExpense, BigDecimal netSavings, Double savingsRate) {
        this.userId = userId;
        this.month = month;
        this.year = year;
        this.totalIncome = totalIncome;
        this.totalExpense = totalExpense;
        this.netSavings = netSavings;
        this.savingsRate = savingsRate;
    }

}
