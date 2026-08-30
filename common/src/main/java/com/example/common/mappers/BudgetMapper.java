package com.example.common.mappers;

import com.example.common.entity.Budget;

import com.example.common.mappers.responses.BudgetView;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class BudgetMapper {

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "categoryId", source = "category.id")
    public abstract BudgetView mapFromForList(Budget budget);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "categoryId", source = "category.id")
    public abstract BudgetView mapFrom(Budget budget);

}