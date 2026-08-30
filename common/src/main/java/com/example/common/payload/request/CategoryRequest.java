package com.example.common.payload.request;

import com.example.common.entity.enums.CategoryType;

import lombok.Data;

@Data
public class CategoryRequest {

    private String name;
    private CategoryType type;

    private Long userId;
}