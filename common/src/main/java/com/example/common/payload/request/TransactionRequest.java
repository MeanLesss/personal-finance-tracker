package com.example.common.payload.request;

import com.example.common.entity.enums.TransactionType;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class TransactionRequest {

    private BigDecimal amount;
    private TransactionType type;
    private String description;
    private LocalDate date;

    private Long accountId;
    private Long categoryId;
}