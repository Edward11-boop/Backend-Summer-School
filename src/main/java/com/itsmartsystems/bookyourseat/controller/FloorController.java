package com.itsmartsystems.bookyourseat.controller;

import com.itsmartsystems.bookyourseat.model.Floor;
import com.itsmartsystems.bookyourseat.repository.FloorRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class FloorController {

    private final FloorRepository floorRepository;

    public FloorController(FloorRepository floorRepository) {
        this.floorRepository = floorRepository;
    }

    @GetMapping("/floors")
    public List<Floor> getAllFloors() {
        return floorRepository.findAll();
    }
}
