package com.my.application.demo.a.pplication.Service;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponse
{
    public String message;

    public LoginResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
