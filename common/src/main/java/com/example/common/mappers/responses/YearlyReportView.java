package com.example.common.mappers.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class YearlyReportView {

    private Integer year;
    private Long userId;
    private Long accountId;
    private String accountName;
    private String currency;

    private BigDecimal totalIncome;
    private BigDecimal totalExpense;
    private BigDecimal netSavings;
    private Double savingsRate;
    private Integer totalTransactions;

    private String highestSpendingMonth;
    private String highestIncomeMonth;

    @Builder.Default
    private List<MonthlyTrendItem> monthlyBreakdown = new ArrayList<>();

    @Builder.Default
    private List<CategoryBreakdownItem> topExpenseCategories = new ArrayList<>();

    @Builder.Default
    private List<CategoryBreakdownItem> topIncomeCategories = new ArrayList<>();

}
