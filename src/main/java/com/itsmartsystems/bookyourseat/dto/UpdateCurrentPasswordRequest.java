package com.itsmartsystems.bookyourseat.dto;

import jakarta.validation.constraints.NotBlank;

public class UpdateCurrentPasswordRequest {

    @NotBlank(message = "New password must not be empty")
    private String newPassword;

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
