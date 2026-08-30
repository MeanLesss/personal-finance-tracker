package com.example.common.entity.enums;

public enum TransactionType {

    INCOME("INCOME"),
    EXPENSE("EXPENSE"),
    TRANSFER("TRANSFER");

    private final String code;

    TransactionType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}