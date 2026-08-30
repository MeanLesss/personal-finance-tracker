package com.example.common.entity.enums;

public enum AccountType {

    CASH("CASH"),
    BANK("BANK"),
    WALLET("WALLET"),
    CREDIT("CREDIT");

    private final String code;

    AccountType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}