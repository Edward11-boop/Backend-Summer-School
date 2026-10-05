package com.itsmartsystems.bookyourseat.controller;

import com.itsmartsystems.bookyourseat.dto.AvailableRoomResponse;
import com.itsmartsystems.bookyourseat.dto.AvailableSeatResponse;
import com.itsmartsystems.bookyourseat.service.AvailabilityService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/availability")
public class AvailabilityController {

    private final AvailabilityService availabilityService;

    public AvailabilityController(AvailabilityService availabilityService) {
        this.availabilityService = availabilityService;
    }

    @GetMapping("/rooms")
    public List<AvailableRoomResponse> getAvailableRooms(
            @RequestParam Long floorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end
    ) {
        return availabilityService.availableRooms(floorId, start, end);
    }

    @GetMapping("/seats")
    public List<AvailableSeatResponse> getAvailableSeats(
            @RequestParam Long roomId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end
    ) {
        return availabilityService.availableSeats(roomId, start, end);
    }
}
