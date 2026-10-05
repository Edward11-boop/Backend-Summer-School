package com.itsmartsystems.bookyourseat.service;

import com.itsmartsystems.bookyourseat.Status;
import com.itsmartsystems.bookyourseat.dto.AvailableRoomResponse;
import com.itsmartsystems.bookyourseat.dto.AvailableSeatResponse;
import com.itsmartsystems.bookyourseat.model.Seat;
import com.itsmartsystems.bookyourseat.repository.InvitationRepository;
import com.itsmartsystems.bookyourseat.repository.ReservationRepository;
import com.itsmartsystems.bookyourseat.repository.RoomRepository;
import com.itsmartsystems.bookyourseat.repository.SeatRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AvailabilityService {
    private final RoomRepository roomRepository;
    private final SeatRepository seatRepository;
    private final ReservationRepository reservationRepository;
    private final InvitationRepository invitationRepository;

    public AvailabilityService(RoomRepository roomRepository, SeatRepository seatRepository,
                               ReservationRepository reservationRepository, InvitationRepository invitationRepository) {
        this.roomRepository = roomRepository;
        this.seatRepository = seatRepository;
        this.reservationRepository = reservationRepository;
        this.invitationRepository = invitationRepository;
    }

    public List<AvailableRoomResponse> availableRooms(Long floorId, LocalDateTime start, LocalDateTime end) {
        validateInterval(start, end);
        return roomRepository.findByFloorId(floorId).stream()
                .filter(room -> seatRepository.findByRoomId(room.getId()).stream()
                        .filter(this::isSelectableSeat)
                        .anyMatch(seat -> isAvailable(seat, start, end)))
                .map(AvailableRoomResponse::new)
                .toList();
    }

    public List<AvailableSeatResponse> availableSeats(Long roomId, LocalDateTime start, LocalDateTime end) {
        validateInterval(start, end);
        return seatRepository.findByRoomId(roomId).stream()
                .filter(this::isSelectableSeat)
                .filter(seat -> isAvailable(seat, start, end))
                .map(AvailableSeatResponse::new)
                .toList();
    }

    private boolean isSelectableSeat(Seat seat) {
        String status = seat.getStatus();
        return "AVAILABLE".equalsIgnoreCase(status) || "ACTIVE".equalsIgnoreCase(status);
    }

    private boolean isAvailable(Seat seat, LocalDateTime start, LocalDateTime end) {
        boolean reservationConflict = !reservationRepository.findBySeat_IdAndStatusInAndStartDateTimeLessThanAndEndDateTimeGreaterThan(
                seat.getId(), List.of(Status.APPROVED, Status.PENDING), end, start).isEmpty();
        boolean invitationConflict = !invitationRepository.findBySeatId_IdAndStatusAndStartDateTimeLessThanAndEndDateTimeGreaterThan(
                seat.getId(), "PENDING", end, start).isEmpty();
        return !reservationConflict && !invitationConflict;
    }

    private void validateInterval(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null || !end.isAfter(start)) {
            throw new IllegalArgumentException("End date-time must be later than start date-time.");
        }
    }
}