package com.itsmartsystems.bookyourseat.dto;

import com.itsmartsystems.bookyourseat.Role;

public class UserDetails {

    private String id;
    private Integer postgresUserId;
    private String name;
    private String email;
    private Role role;
    private String phoneNumber;
    private Integer departmentId;
    private String departmentName;

    public UserDetails(String id, Integer postgresUserId, String name, String email, Role role) {
        this.id = id;
        this.postgresUserId = postgresUserId;
        this.name = name;
        this.email = email;
        this.role = role;
    }

    public UserDetails(String id, String name, String email, Role role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
    }

    public UserDetails(String name, String email, Role role) {
        this.name = name;
        this.email = email;
        this.role = role;
    }

    public UserDetails() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getPostgresUserId() {
        return postgresUserId;
    }

    public void setPostgresUserId(Integer postgresUserId) {
        this.postgresUserId = postgresUserId;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Integer getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Integer departmentId) {
        this.departmentId = departmentId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }
}
