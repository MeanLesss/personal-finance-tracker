package com.example.common.mappers;

import com.example.common.entity.Account;

import com.example.common.mappers.responses.AccountView;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class AccountMapper {

    @Mapping(target = "userId", source = "user.id")
    public abstract AccountView mapFromForList(Account account);

    @Mapping(target = "userId", source = "user.id")
    public abstract AccountView mapFrom(Account account);

}