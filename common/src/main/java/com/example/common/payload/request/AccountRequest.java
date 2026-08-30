package com.example.common.payload.request;

import com.example.common.entity.enums.AccountType;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AccountRequest {

    private String accountName;
    private AccountType accountType;
    private BigDecimal balance;
    private String currency;

    private Long userId;
}