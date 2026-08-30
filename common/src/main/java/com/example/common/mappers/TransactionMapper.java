package com.example.common.mappers;

import com.example.common.entity.Transaction;

import com.example.common.mappers.responses.TransactionView;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class TransactionMapper {

    @Mapping(target = "accountId", source = "account.id")
    @Mapping(target = "categoryId", source = "category.id")
    public abstract TransactionView mapFromForList(Transaction transaction);

    @Mapping(target = "accountId", source = "account.id")
    @Mapping(target = "categoryId", source = "category.id")
    public abstract TransactionView mapFrom(Transaction transaction);

}