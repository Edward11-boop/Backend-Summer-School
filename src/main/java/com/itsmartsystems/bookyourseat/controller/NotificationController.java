package com.itsmartsystems.bookyourseat.controller;

import com.itsmartsystems.bookyourseat.dto.NotificationResponse;
import com.itsmartsystems.bookyourseat.dto.ReservationResponse;
import com.itsmartsystems.bookyourseat.model.Notification;
import com.itsmartsystems.bookyourseat.model.PostgresUser;
import com.itsmartsystems.bookyourseat.model.Reservation;
import com.itsmartsystems.bookyourseat.repository.PostgresUserRepository;
import com.itsmartsystems.bookyourseat.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final PostgresUserRepository postgresUserRepository;

    public NotificationController(NotificationService notificationService, PostgresUserRepository postgresUserRepository) {
        this.notificationService = notificationService;
        this.postgresUserRepository = postgresUserRepository;
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<NotificationResponse>> getNotificationsForUser(@PathVariable Integer userId) {
        List<NotificationResponse> notifications = notificationService.getNotificationsForUser(userId)
                .stream()
                .map(NotificationResponse::new)
                .toList();

        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/user/{userId}/unread")
    public ResponseEntity<List<NotificationResponse>> getUnreadNotificationsForUser(@PathVariable Integer userId) {
        List<NotificationResponse> notifications = notificationService.getUnreadNotificationsForUser(userId)
                .stream()
                .map(NotificationResponse::new)
                .toList();

        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/user/{userId}/unread-count")
    public ResponseEntity<Map<String, Long>> countUnreadNotificationsForUser(@PathVariable Integer userId) {
        return ResponseEntity.ok(Map.of("count", notificationService.countUnreadNotificationsForUser(userId)));
    }

    @GetMapping("/me")
    public ResponseEntity<List<NotificationResponse>> getNotificationsForCurrentUser() {
        Integer userId = getCurrentUser().getId();
        List<NotificationResponse> notifications = notificationService.getNotificationsForUser(userId)
                .stream()
                .map(NotificationResponse::new)
                .toList();

        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/me/unread")
    public ResponseEntity<List<NotificationResponse>> getUnreadNotificationsForCurrentUser() {
        Integer userId = getCurrentUser().getId();
        List<NotificationResponse> notifications = notificationService.getUnreadNotificationsForUser(userId)
                .stream()
                .map(NotificationResponse::new)
                .toList();

        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/me/unread-count")
    public ResponseEntity<Map<String, Long>> countUnreadNotificationsForCurrentUser() {
        return ResponseEntity.ok(Map.of("count", notificationService.countUnreadNotificationsForUser(getCurrentUser().getId())));
    }

    @PutMapping("/{notificationId}/read")
    public ResponseEntity<NotificationResponse> markAsRead(@PathVariable Long notificationId) {
        Notification notification = notificationService.markAsRead(notificationId);
        return ResponseEntity.ok(new NotificationResponse(notification));
    }

    @PostMapping("/{notificationId}/accept")
    public ResponseEntity<ReservationResponse> acceptNotification(@PathVariable Long notificationId) {
        Reservation created = notificationService.acceptColleaguesNotification(notificationId, getCurrentUser());
        return ResponseEntity.ok(new ReservationResponse(created));
    }

    @PostMapping("/{notificationId}/decline")
    public ResponseEntity<NotificationResponse> declineNotification(@PathVariable Long notificationId) {
        Notification notification = notificationService.declineNotification(notificationId);
        return ResponseEntity.ok(new NotificationResponse(notification));
    }

    @DeleteMapping("/{notificationId}")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long notificationId) {
        notificationService.deleteNotification(notificationId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/past")
    public ResponseEntity<Void> deletePastInvitationNotifications() {
        notificationService.deletePastInvitationNotifications();
        return ResponseEntity.noContent().build();
    }

    private PostgresUser getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null || "anonymousUser".equals(auth.getName())) {
            throw new IllegalArgumentException("User not found.");
        }

        return postgresUserRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new IllegalArgumentException("User not found."));
    }
}
