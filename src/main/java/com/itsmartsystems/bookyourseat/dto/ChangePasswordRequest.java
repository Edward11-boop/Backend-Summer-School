package com.itsmartsystems.bookyourseat.dto;

import jakarta.validation.constraints.NotBlank;

public class ChangePasswordRequest {


    @NotBlank(message = "Email must not be empty")
    private String email ;
    @NotBlank(message = "OldPassword must not be empty")
    private String oldPassword;

    @NotBlank(message = "NewPassword must not be empty")
    private String newPassword;

    public ChangePasswordRequest(String email , String oldPassword , String newPassword)
    {
        this.email = email;
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
    }

    public String getEmail() {return this.email;}
    public String getOldPassword() {return this.oldPassword;}
    public String getNewPassword() {return this.newPassword;}

}
