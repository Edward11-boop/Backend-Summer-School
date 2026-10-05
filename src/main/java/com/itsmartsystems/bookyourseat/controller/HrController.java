package com.itsmartsystems.bookyourseat.controller;

import com.itsmartsystems.bookyourseat.dto.EmployeeAttendanceDto;
import com.itsmartsystems.bookyourseat.dto.ColleaguePreferenceDto;
import com.itsmartsystems.bookyourseat.dto.HrReportsDto;
import com.itsmartsystems.bookyourseat.dto.UserDetails;
import com.itsmartsystems.bookyourseat.service.HrService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/hr")
public class HrController {

    private final HrService hrService;

    public HrController(HrService hrService) {
        this.hrService = hrService;
    }

    @GetMapping("/users")
    public List<UserDetails> getAllUsers() {
        return hrService.getAllUsers();
    }

    @GetMapping("/attendance-history")
    public List<EmployeeAttendanceDto> getAttendanceHistory(
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String date
    ) {
        return hrService.getAttendanceHistory(department, date);
    }

    @GetMapping("/reports")
    public HrReportsDto getReports() {
        return hrService.getReports();
    }

    @GetMapping("/preferences")
    public List<ColleaguePreferenceDto> getPreferences(
            @RequestParam(required = false) String name
    ) {
        return hrService.getPreferences(name);
    }
}