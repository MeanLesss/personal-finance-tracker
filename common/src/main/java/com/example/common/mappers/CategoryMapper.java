package com.example.common.mappers;

import com.example.common.entity.Category;

import com.example.common.mappers.responses.CategoryView;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class CategoryMapper {

    @Mapping(target = "userId", source = "user.id")
    public abstract CategoryView mapFromForList(Category category);

    @Mapping(target = "userId", source = "user.id")
    public abstract CategoryView mapFrom(Category category);

}