package com.itsmartsystems.bookyourseat.dto;


import jakarta.validation.constraints.NotBlank;

public class LoginRequest {


    @NotBlank(message = "Email must not be empty")
    private String email ;

    @NotBlank(message = "Password must not be empty")
    private String password;

    public LoginRequest(String email, String password){
        this.email = email;
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

}
