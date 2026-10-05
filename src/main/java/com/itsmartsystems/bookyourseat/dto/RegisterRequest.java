package com.itsmartsystems.bookyourseat.dto;


import com.itsmartsystems.bookyourseat.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class RegisterRequest {

    @NotBlank(message = "Email must not be empty")
    @Email(message = "Invalid email format")
    private String email ;

    @NotBlank(message = "Name must not be empty !")
    private String name ;

    @NotBlank(message = "Password must not be empty !")
    private String password;
    private Role role;


    public RegisterRequest(String email, String name , String password, Role role){

        this.email = email;
        this.name = name;
        this.password = password;
        this.role = role;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public Role getRole() {
        return role;
    }

    public String getName() {
        return name;
    }
}
