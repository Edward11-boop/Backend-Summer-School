package com.itsmartsystems.bookyourseat.dto;

import jakarta.validation.constraints.NotBlank;

public class EmailRequest {

    @NotBlank(message = "Email must not be empty ")
    private String email ;
    public EmailRequest(String email)
    {
        this.email = email;
    }

    public String getEmail() {return this.email ; }

}
