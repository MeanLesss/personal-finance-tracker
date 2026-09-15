package com.example.common.mappers.responses;

import com.example.common.entity.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CategoryBreakdownView {

    private String period;
    private Long userId;
    private Long accountId;
    private String accountName;
    private TransactionType type;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal totalAmount;

    @Builder.Default
    private List<CategoryBreakdownItem> categories = new ArrayList<>();

    public CategoryBreakdownView(Long userId, Integer month, Integer year, BigDecimal totalExpense, List<CategoryBreakdownItem> categories) {
        this.userId = userId;
        this.period = (month != null && year != null) ? month + "/" + year : (year != null ? String.valueOf(year) : "");
        this.type = TransactionType.EXPENSE;
        this.totalAmount = totalExpense;
        this.categories = categories;
    }

}
