package com.itsmartsystems.bookyourseat.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class PostgresUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "mongo_user_id", unique = true, nullable = false)
    private String mongoUserId;

    @Column(name = "department_id")
    private Integer departmentId;

    private String name;
    private String role;
    private String email;

    @Column(name = "phone_number")
    private String phoneNumber;

    public PostgresUser() {
    }

    public PostgresUser(
            String mongoUserId,
            Integer departmentId,
            String name,
            String role,
            String email,
            String phoneNumber
    ) {
        this.mongoUserId = mongoUserId;
        this.departmentId = departmentId;
        this.name = name;
        this.role = role;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMongoUserId() {
        return this.mongoUserId;
    }

    public void setMongoUserId(String mongoUserId) {
        this.mongoUserId = mongoUserId;
    }

    public Integer getDepartmentId() {
        return this.departmentId;
    }

    public void setDepartmentId(Integer departmentId) {
        this.departmentId = departmentId;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return this.role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}