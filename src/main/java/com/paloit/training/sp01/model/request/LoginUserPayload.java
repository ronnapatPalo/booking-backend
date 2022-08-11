package com.paloit.training.sp01.model.request;

import lombok.Data;

@Data
public class LoginUserPayload {
    private String email;
    private String password;
}
