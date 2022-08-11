package com.paloit.training.sp01.model.request;

import lombok.Data;

import javax.persistence.Entity;

@Data
public class CreateUserPayload {
    private String firstname;
    private String lastname;
    private String email;
    private String password;
}
