package com.example.common.mappers.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountBalanceView {

    private Long accountId;
    private String accountName;
    private BigDecimal balance;
    private String currency;

}
