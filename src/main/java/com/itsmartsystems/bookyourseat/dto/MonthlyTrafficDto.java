package com.itsmartsystems.bookyourseat.dto;

public class MonthlyTrafficDto {
    private String day;
    private int employeesCount;

    public MonthlyTrafficDto() {
    }

    public MonthlyTrafficDto(String day, int employeesCount) {
        this.day = day;
        this.employeesCount = employeesCount;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public int getEmployeesCount() {
        return employeesCount;
    }

    public void setEmployeesCount(int employeesCount) {
        this.employeesCount = employeesCount;
    }
}