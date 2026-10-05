package com.itsmartsystems.bookyourseat.controller;

import com.itsmartsystems.bookyourseat.dto.ColleagueDto;
import com.itsmartsystems.bookyourseat.dto.InvitationRequest;
import com.itsmartsystems.bookyourseat.model.Invitation;
import com.itsmartsystems.bookyourseat.model.PostgresUser;
import com.itsmartsystems.bookyourseat.repository.PostgresUserRepository;
import com.itsmartsystems.bookyourseat.service.InvitationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/invitations")
public class InvitationController {

    private final InvitationService invitationService;
    private final PostgresUserRepository postgresUserRepository;

    public InvitationController(InvitationService invitationService, PostgresUserRepository postgresUserRepository) {
        this.invitationService = invitationService;
        this.postgresUserRepository = postgresUserRepository;
    }

    @PostMapping
    public ResponseEntity<Invitation> createInvitation(@RequestBody InvitationRequest request) {
        PostgresUser currentUser = getCurrentUserOrNull();
        Invitation savedInvitation = currentUser == null
                ? invitationService.createInvitation(request)
                : invitationService.createInvitation(currentUser, request);
        return ResponseEntity.ok(savedInvitation);
    }

    @GetMapping("/colleagues")
    public ResponseEntity<List<ColleagueDto>> getColleagues(@RequestParam(required = false) LocalDate date) {
        PostgresUser currentUser = getCurrentUser();
        return ResponseEntity.ok(invitationService.getEligibleColleagues(currentUser, date));
    }

    @GetMapping("/pending")
    public ResponseEntity<List<Invitation>> getPendingInvitationsForCurrentUser() {
        List<Invitation> pendingList = invitationService.getPendingInvitationsForUser(getCurrentUser().getId());
        return ResponseEntity.ok(pendingList);
    }

    @GetMapping("/pending/{userId}")
    public ResponseEntity<List<Invitation>> getPendingInvitations(@PathVariable Integer userId) {
        List<Invitation> pendingList = invitationService.getPendingInvitationsForUser(userId);
        return ResponseEntity.ok(pendingList);
    }

    @PostMapping("/{id}/accept")
    public ResponseEntity<Void> acceptInvitationForCurrentUser(@PathVariable Long id) {
        invitationService.acceptInvitation(id, getCurrentUser().getId());
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/accept")
    public ResponseEntity<String> acceptInvitation(@PathVariable Long id) {
        PostgresUser currentUser = getCurrentUserOrNull();
        if (currentUser == null) {
            invitationService.acceptInvitation(id);
        } else {
            invitationService.acceptInvitation(id, currentUser.getId());
        }
        return ResponseEntity.ok("Invitatia a fost acceptata si rezervarea a fost creata automat.");
    }

    @PostMapping("/{id}/decline")
    public ResponseEntity<Void> declineInvitationForCurrentUser(@PathVariable Long id) {
        invitationService.declineInvitation(id, getCurrentUser().getId());
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/decline")
    public ResponseEntity<String> declineInvitation(@PathVariable Long id) {
        PostgresUser currentUser = getCurrentUserOrNull();
        if (currentUser == null) {
            invitationService.declineInvitation(id);
        } else {
            invitationService.declineInvitation(id, currentUser.getId());
        }
        return ResponseEntity.ok("Invitatia a fost refuzata.");
    }

    private PostgresUser getCurrentUser() {
        PostgresUser currentUser = getCurrentUserOrNull();
        if (currentUser == null) {
            throw new IllegalArgumentException("User not found.");
        }
        return currentUser;
    }

    private PostgresUser getCurrentUserOrNull() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null || "anonymousUser".equals(auth.getName())) {
            return null;
        }

        return postgresUserRepository.findByEmail(auth.getName()).orElse(null);
    }
}
