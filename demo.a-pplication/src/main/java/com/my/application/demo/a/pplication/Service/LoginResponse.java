package com.my.application.demo.a.pplication.Service;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponse
{
    public String message;
    public String message1;
    public LoginResponse(String message,String message1)
    {
        this.message=message;
        this.message1=message1;
    }

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
