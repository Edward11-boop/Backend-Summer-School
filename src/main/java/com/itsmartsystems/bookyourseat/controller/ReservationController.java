package com.itsmartsystems.bookyourseat.controller;

import com.itsmartsystems.bookyourseat.dto.ReservationRequest;
import com.itsmartsystems.bookyourseat.dto.ReservationResponse;
import com.itsmartsystems.bookyourseat.model.PostgresUser;
import com.itsmartsystems.bookyourseat.model.Reservation;
import com.itsmartsystems.bookyourseat.repository.PostgresUserRepository;
import com.itsmartsystems.bookyourseat.service.ReservationService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/reservations")
public class ReservationController {

    private final ReservationService reservationService;
    private final PostgresUserRepository postgresUserRepository;

    public ReservationController(ReservationService reservationService, PostgresUserRepository postgresUserRepository) {
        this.reservationService = reservationService;
        this.postgresUserRepository = postgresUserRepository;
    }

    private PostgresUser getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return postgresUserRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("This user does not exist"));
    }

    private void verify(PostgresUser user) {
        String role = user.getRole();
        if (!("PM".equals(role) || "CEO".equals(role) || "MANAGER".equals(role) || "HR".equals(role))) {
            throw new IllegalArgumentException("You do not have permissions!");
        }
    }

    @PostMapping
    public Reservation createReservation(@RequestBody ReservationRequest reservationRequest) {
        PostgresUser user = getCurrentUser();
        return reservationService.createReservation(
                user,
                reservationRequest.getRoomCode(),
                reservationRequest.getSeatCode(),
                reservationRequest.getStart(),
                reservationRequest.getEnd(),
                reservationRequest.getRecurrence()
        );
    }

    @PutMapping("/{id}")
    public Reservation modifyReservation(@PathVariable Long id, @RequestBody ReservationRequest reservationRequest) {
        PostgresUser user = getCurrentUser();
        return reservationService.modifyReservation(
                user,
                id,
                reservationRequest.getRoomCode(),
                reservationRequest.getSeatCode(),
                reservationRequest.getStart(),
                reservationRequest.getEnd(),
                reservationRequest.getRecurrence()
        );
    }

    @DeleteMapping("/{id}")
    public void deleteReservation(@PathVariable Long id) {
        reservationService.deleteReservation(id);
    }

    @PutMapping("/approve/{id}")
    public Reservation approveReservation(@PathVariable Long id) {
        verify(getCurrentUser());
        return reservationService.approveReservation(id);
    }

    @PutMapping("/reject/{id}")
    public Reservation rejectReservation(@PathVariable Long id) {
        verify(getCurrentUser());
        return reservationService.rejectReservation(id);
    }

    @PutMapping("/cancel/{id}")
    public Reservation cancelReservation(@PathVariable Long id) {
        PostgresUser user = getCurrentUser();
        return reservationService.cancelOwnReservation(id, user);
    }

    @GetMapping("/approve-link/{id}")
    public ResponseEntity<String> approveViaLink(@PathVariable Long id, @RequestParam String token) {
        try {
            reservationService.approveByToken(id, token);
            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_HTML)
                    .body(confirmationPage("Rezervare aprobata", "Ai aprobat cu succes aceasta rezervare.", "#24f928"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .contentType(MediaType.TEXT_HTML)
                    .body(confirmationPage("Link invalid", e.getMessage(), "#DC2626"));
        }
    }

    @GetMapping("/decline-link/{id}")
    public ResponseEntity<String> declineViaLink(@PathVariable Long id, @RequestParam String token) {
        try {
            reservationService.declineByToken(id, token);
            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_HTML)
                    .body(confirmationPage("Rezervare respinsa", "Ai respins aceasta rezervare.", "#DC2626"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .contentType(MediaType.TEXT_HTML)
                    .body(confirmationPage("Link invalid", e.getMessage(), "#DC2626"));
        }
    }

    private String confirmationPage(String title, String message, String color) {
        return "<html><body style='font-family:Arial;text-align:center;padding:60px;'>"
                + "<h1 style='color:" + color + ";'>" + title + "</h1>"
                + "<p>" + message + "</p></body></html>";
    }

    @GetMapping("/history")
    public List<Reservation> historyReservation() {
        PostgresUser user = getCurrentUser();
        return reservationService.historyReservation(user.getId());
    }

    @GetMapping("/active")
    public List<ReservationResponse> activeReservations(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end
    ) {
        return reservationService.activeReservations(start, end)
                .stream()
                .map(ReservationResponse::new)
                .toList();
    }

    @GetMapping("/pending")
    public List<Reservation> pendingReservations() {
        verify(getCurrentUser());
        return reservationService.pendingReservations();
    }

    @GetMapping("/approved")
    public List<Reservation> approvedReservations() {
        verify(getCurrentUser());
        return reservationService.approvedReservations();
    }

    @GetMapping("/rejected")
    public List<Reservation> rejectedReservations() {
        verify(getCurrentUser());
        return reservationService.rejectedReservations();
    }
}
