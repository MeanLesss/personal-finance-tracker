package com.example.coreapi.service;

import com.example.common.mappers.responses.UserView;
import com.example.common.payload.request.UserRequest;
import com.example.common.util.Pagination;

import java.util.List;

public interface UserService {

    UserView save(UserRequest request);

    UserView findById(Long id);

    List<UserView> findAll(Pagination pagination);

    UserView update(Long id, UserRequest request);

    void delete(Long id);

}