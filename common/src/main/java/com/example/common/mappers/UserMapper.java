package com.example.common.mappers;

import com.example.common.entity.User;

import com.example.common.mappers.responses.UserView;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class UserMapper {

    public abstract UserView mapFromForList(User user);

    public abstract UserView mapFrom(User user);

}