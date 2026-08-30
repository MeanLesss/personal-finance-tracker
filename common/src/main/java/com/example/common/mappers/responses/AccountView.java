package com.example.common.mappers.responses;

import com.example.common.entity.enums.AccountType;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AccountView extends AuditableView {

    private String accountName;
    private AccountType accountType;
    private BigDecimal balance;
    private String currency;

    private Long userId;
}