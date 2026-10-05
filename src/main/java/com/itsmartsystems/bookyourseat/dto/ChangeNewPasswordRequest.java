package com.itsmartsystems.bookyourseat.dto;

import jakarta.validation.constraints.NotBlank;

public class ChangeNewPasswordRequest {

    @NotBlank(message = "New password must not be empty")
    private String newPassword;
    @NotBlank(message = "Old password must not be empty")
    private String cNewPassword;
    @NotBlank(message = "Token must not be empty")
    private String token ;

    public ChangeNewPasswordRequest(String newPassword , String cNewPassword , String token){
        this.newPassword = newPassword;
        this.cNewPassword = cNewPassword;
        this.token = token;
    }
    public String getNewPassword() {return this.newPassword;}
    public String getcNewPassword() {return this.cNewPassword;}
    public String token() {return this.token;}


}
