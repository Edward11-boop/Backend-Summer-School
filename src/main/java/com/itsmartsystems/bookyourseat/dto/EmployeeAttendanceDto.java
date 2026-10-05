package com.itsmartsystems.bookyourseat.dto;

import java.util.List;

public class EmployeeAttendanceDto {
    private String id;
    private String name;
    private String department;
    private int presenceDays;
    private int totalWorkingDays;
    private List<String> attendanceDates;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public int getPresenceDays() {
        return presenceDays;
    }

    public void setPresenceDays(int presenceDays) {
        this.presenceDays = presenceDays;
    }

    public int getTotalWorkingDays() {
        return totalWorkingDays;
    }

    public void setTotalWorkingDays(int totalWorkingDays) {
        this.totalWorkingDays = totalWorkingDays;
    }

    public List<String> getAttendanceDates() {
        return attendanceDates;
    }

    public void setAttendanceDates(List<String> attendanceDates) {
        this.attendanceDates = attendanceDates;
    }
}