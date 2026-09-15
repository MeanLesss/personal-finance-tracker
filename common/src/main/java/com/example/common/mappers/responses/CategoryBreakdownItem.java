package com.example.common.mappers.responses;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CategoryBreakdownItem {

    private Long categoryId;
    private String categoryName;
    private BigDecimal totalAmount;
    private Double percentageOfTotal;
    private Integer transactionCount;

    public CategoryBreakdownItem(Long categoryId, String categoryName, BigDecimal totalAmount, Double percentageOfTotal) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.totalAmount = totalAmount;
        this.percentageOfTotal = percentageOfTotal;
        this.transactionCount = 0;
    }

}
