package com.itsmartsystems.bookyourseat.service;

import com.itsmartsystems.bookyourseat.model.PostgresUser;
import com.itsmartsystems.bookyourseat.model.Reservation;
import com.itsmartsystems.bookyourseat.model.Room;
import com.itsmartsystems.bookyourseat.model.Seat;
import com.itsmartsystems.bookyourseat.repository.PostgresUserRepository;
import com.itsmartsystems.bookyourseat.repository.ReservationRepository;
import com.itsmartsystems.bookyourseat.repository.RoomRepository;
import com.itsmartsystems.bookyourseat.repository.SeatRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private SeatRepository seatRepository;

    @Mock
    private PostgresUserRepository postgresUserRepository;

    @Mock
    private NotificationService notificationService;

    @Mock
    private N8nService n8nService;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private ReservationService reservationService;

    @Test
    void createReservation_shouldSendConfirmationEmailToUser() {
        Room room = new Room();
        room.setId(1L);
        room.setCode("A1");
        room.setName("Main room");

        Seat seat = new Seat();
        seat.setId(10L);
        seat.setCode("S1");
        seat.setRoom(room);

        PostgresUser user = new PostgresUser();
        user.setId(7);
        user.setEmail("test@example.com");
        user.setName("Test User");

        LocalDateTime start = LocalDateTime.of(2026, 8, 25, 9, 0);
        LocalDateTime end = LocalDateTime.of(2026, 8, 25, 10, 0);

        when(roomRepository.findByCode("A1")).thenReturn(Optional.of(room));
        when(seatRepository.findByCodeForUpdate("S1")).thenReturn(Optional.of(seat));
        when(reservationRepository.findBySeat_IdAndStatusInAndStartDateTimeLessThanAndEndDateTimeGreaterThan(
                eq(10L), any(), eq(end), eq(start)
        )).thenReturn(Collections.emptyList());
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(invocation -> {
            Reservation reservation = invocation.getArgument(0);
            reservation.setId(99L);
            return reservation;
        });

        reservationService.createReservation(user, "A1", "S1", start, end, 0);

        verify(emailService).sendReservationConfirmation(eq("test@example.com"), any(Reservation.class));
    }

    @Test
    void approveByToken_shouldApproveWhenTokenMatches() {
        Reservation reservation = new Reservation();
        reservation.setId(42L);
        reservation.setStatus(com.itsmartsystems.bookyourseat.Status.PENDING);
        reservation.setApprovalToken("secret-token");

        when(reservationRepository.findById(42L)).thenReturn(Optional.of(reservation));
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Reservation updated = reservationService.approveByToken(42L, "secret-token");

        assertEquals(com.itsmartsystems.bookyourseat.Status.APPROVED, updated.getStatus());
        assertEquals("secret-token", updated.getApprovalToken());
    }

    @Test
    void approveByToken_shouldRejectInvalidToken() {
        Reservation reservation = new Reservation();
        reservation.setId(42L);
        reservation.setStatus(com.itsmartsystems.bookyourseat.Status.PENDING);
        reservation.setApprovalToken("correct-token");

        when(reservationRepository.findById(42L)).thenReturn(Optional.of(reservation));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> reservationService.approveByToken(42L, "wrong-token"));

        assertEquals("Invalid or expired approval token", ex.getMessage());
    }
}
