package com.itsmartsystems.bookyourseat.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class UpdatePhoneRequest {

    @NotBlank(message = "Phone number must not be empty")
    @Pattern(regexp = "^(?:(?:0|\\+40)[2-9](?:[\\s().-]?\\d){8}|(?:0|\\+373)[2-9](?:[\\s().-]?\\d){7})$", message = "Phone number must be Romanian or Moldovan")
    private String phoneNumber;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
