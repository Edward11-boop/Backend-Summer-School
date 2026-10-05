package com.itsmartsystems.bookyourseat.dto;

public class ForgotPasswordRequest {

    private String password ;
    private String confirmPassword;
    public ForgotPasswordRequest(String password , String confirmPassword)
    {
        this.password = password;
        this.confirmPassword = confirmPassword;
    }

    public String getPassword(){return this.password;}
    public String getConfirmPassword() {return this.confirmPassword;}



}
