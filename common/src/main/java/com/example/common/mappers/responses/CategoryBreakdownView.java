package com.example.common.mappers.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryBreakdownView {

    private Long userId;
    private Integer month;
    private Integer year;
    private BigDecimal totalExpense;
    private List<CategoryBreakdownItem> categories = new ArrayList<>();

}
