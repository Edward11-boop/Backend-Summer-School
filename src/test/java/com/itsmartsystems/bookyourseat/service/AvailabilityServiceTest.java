package com.itsmartsystems.bookyourseat.service;

import com.itsmartsystems.bookyourseat.dto.AvailableRoomResponse;
import com.itsmartsystems.bookyourseat.model.Floor;
import com.itsmartsystems.bookyourseat.model.Room;
import com.itsmartsystems.bookyourseat.model.Seat;
import com.itsmartsystems.bookyourseat.repository.InvitationRepository;
import com.itsmartsystems.bookyourseat.repository.ReservationRepository;
import com.itsmartsystems.bookyourseat.repository.RoomRepository;
import com.itsmartsystems.bookyourseat.repository.SeatRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AvailabilityServiceTest {

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private SeatRepository seatRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private InvitationRepository invitationRepository;

    @InjectMocks
    private AvailabilityService availabilityService;

    @Test
    void availableRooms_returnsRoomWhenAtLeastOneSeatIsAvailable() {
        Floor floor = new Floor();
        floor.setId(2L);
        floor.setName("Etaj 2");

        Room room = new Room();
        room.setId(10L);
        room.setFloor_id(floor);
        room.setCode("A-1");
        room.setName("Camera A1");
        room.setType("STANDARD");
        room.setCapacity(10L);

        Seat seat = new Seat();
        seat.setId(200L);
        seat.setRoom(room);
        seat.setCode("A-01");
        seat.setStatus("AVAILABLE");
        seat.setType("DESK");

        LocalDateTime start = LocalDateTime.of(2026, 8, 20, 9, 0);
        LocalDateTime end = LocalDateTime.of(2026, 8, 20, 11, 0);

        when(roomRepository.findByFloorId(2L)).thenReturn(List.of(room));
        when(seatRepository.findByRoomId(10L)).thenReturn(List.of(seat));
        when(reservationRepository.findBySeat_IdAndStatusInAndStartDateTimeLessThanAndEndDateTimeGreaterThan(
                eq(200L), any(), eq(end), eq(start))).thenReturn(List.of());
        when(invitationRepository.findBySeatId_IdAndStatusAndStartDateTimeLessThanAndEndDateTimeGreaterThan(
                eq(200L), eq("PENDING"), eq(end), eq(start))).thenReturn(List.of());

        List<AvailableRoomResponse> result = availabilityService.availableRooms(2L, start, end);

        assertEquals(1, result.size());
        assertEquals(10L, result.get(0).id());
    }

    @Test
    void availableRooms_skipsRoomWithoutAvailableSeat() {
        Floor floor = new Floor();
        floor.setId(3L);
        floor.setName("Etaj 3");

        Room room = new Room();
        room.setId(11L);
        room.setFloor_id(floor);
        room.setCode("B-1");
        room.setName("Camera B1");
        room.setType("STANDARD");
        room.setCapacity(12L);

        Seat seat = new Seat();
        seat.setId(201L);
        seat.setRoom(room);
        seat.setCode("B-01");
        seat.setStatus("OCCUPIED");
        seat.setType("DESK");

        LocalDateTime start = LocalDateTime.of(2026, 8, 20, 14, 0);
        LocalDateTime end = LocalDateTime.of(2026, 8, 20, 16, 0);

        when(roomRepository.findByFloorId(3L)).thenReturn(List.of(room));
        when(seatRepository.findByRoomId(11L)).thenReturn(List.of(seat));

        List<AvailableRoomResponse> result = availabilityService.availableRooms(3L, start, end);

        assertEquals(0, result.size());
    }
}
