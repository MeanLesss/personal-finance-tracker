package com.example.common.mappers.responses;

import lombok.Data;

@Data
public class UserView extends AuditableView {

    private String name;
    private String email;
    private String password;
}