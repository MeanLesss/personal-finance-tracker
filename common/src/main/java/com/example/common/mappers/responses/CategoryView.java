package com.example.common.mappers.responses;

import com.example.common.entity.enums.CategoryType;

import lombok.Data;

@Data
public class CategoryView extends AuditableView {

    private String name;
    private CategoryType type;

    private Long userId;
}