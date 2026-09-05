package com.example.common.mappers.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NetWorthView {

    private Long userId;
    private BigDecimal totalNetWorth;
    private String currency;
    private int accountCount;

}
