package com.example.common.mappers.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryBreakdownItem {

    private Long categoryId;
    private String categoryName;
    private BigDecimal totalSpent;
    private Double percentageOfTotal;

}
