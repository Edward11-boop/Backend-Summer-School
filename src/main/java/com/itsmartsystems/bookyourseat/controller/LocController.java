package com.itsmartsystems.bookyourseat.controller;

import com.itsmartsystems.bookyourseat.Status;
import com.itsmartsystems.bookyourseat.model.Reservation;
import com.itsmartsystems.bookyourseat.model.Seat;
import com.itsmartsystems.bookyourseat.repository.ReservationRepository;
import com.itsmartsystems.bookyourseat.repository.SeatRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
public class LocController {

    private final SeatRepository locRepository;
    private final ReservationRepository reservationRepository;

    public LocController(SeatRepository locRepository, ReservationRepository reservationRepository) {
        this.locRepository = locRepository;
        this.reservationRepository = reservationRepository;
    }

    @GetMapping("/locuri")
    public List<Seat> getAllLocuri() {
        syncOccupiedSeatsFromApprovedReservations();
        return locRepository.findAll();
    }

    @GetMapping("/locuri/room/{salaId}")
    public List<Seat> getLocuriDinSala(@PathVariable String salaId) {
        syncOccupiedSeatsFromApprovedReservations();
        return locRepository.findBySalaId(salaId);
    }

    private void syncOccupiedSeatsFromApprovedReservations() {
        Set<Long> occupiedSeatIds = reservationRepository.findByStatus(Status.APPROVED)
                .stream()
                .map(Reservation::getSeat)
                .filter(seat -> seat != null && seat.getId() != null)
                .map(Seat::getId)
                .collect(Collectors.toSet());

        if (occupiedSeatIds.isEmpty()) {
            return;
        }

        List<Seat> seatsToUpdate = locRepository.findAll()
                .stream()
                .filter(seat -> occupiedSeatIds.contains(seat.getId()))
                .filter(seat -> !"OCCUPIED".equalsIgnoreCase(seat.getStatus()))
                .peek(seat -> seat.setStatus("OCCUPIED"))
                .toList();

        if (!seatsToUpdate.isEmpty()) {
            locRepository.saveAll(seatsToUpdate);
        }
    }
}