package com.example.common.entity.enums;

public enum CategoryType {

    INCOME("INCOME"),
    EXPENSE("EXPENSE");

    private final String code;

    CategoryType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}