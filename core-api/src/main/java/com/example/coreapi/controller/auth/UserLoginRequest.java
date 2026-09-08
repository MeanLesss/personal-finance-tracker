package com.example.coreapi.controller.auth;

import lombok.Data;

@Data
public class UserLoginRequest {

    private String email;
    private String password;
}