package com.itsmartsystems.bookyourseat.controller;

import com.itsmartsystems.bookyourseat.model.Room;
import com.itsmartsystems.bookyourseat.repository.RoomRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SalaController {

    private final RoomRepository roomRepository;

    public SalaController(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    @GetMapping("/sali")
    public List<Room> getAllSali() {
        return roomRepository.findAll();
    }
}