package com.itsmartsystems.bookyourseat.service;

import com.itsmartsystems.bookyourseat.Status;

import com.itsmartsystems.bookyourseat.model.Invitation;
import com.itsmartsystems.bookyourseat.model.Notification;
import com.itsmartsystems.bookyourseat.model.PostgresUser;
import com.itsmartsystems.bookyourseat.model.Reservation;
import com.itsmartsystems.bookyourseat.model.Seat;
import com.itsmartsystems.bookyourseat.repository.NotificationRepository;
import com.itsmartsystems.bookyourseat.repository.ReservationRepository;
import com.itsmartsystems.bookyourseat.repository.SeatRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final ReservationRepository reservationRepository;
    private final SeatRepository seatRepository;

    public NotificationService(NotificationRepository notificationRepository,
            ReservationRepository reservationRepository,
            SeatRepository seatRepository) {
        this.notificationRepository = notificationRepository;
        this.reservationRepository = reservationRepository;
        this.seatRepository = seatRepository;
    }

    @Transactional(readOnly = true)
    public List<Notification> getNotificationsForUser(Integer userId) {
        return notificationRepository.findWithDetailsByUserIdOrderByCreatedAtDesc(userId);
    }

    @Transactional(readOnly = true)
    public List<Notification> getUnreadNotificationsForUser(Integer userId) {
        return notificationRepository.findUnreadWithDetailsByUserId(userId);
    }

    public long countUnreadNotificationsForUser(Integer userId) {
        return notificationRepository.countByUser_IdAndIsReadFalse(userId);
    }

    public Notification markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notificarea nu exista."));

        notification.setRead(true);
        return notificationRepository.save(notification);
    }

    public Notification declineNotification(Long notificationId) {
        return markAsRead(notificationId);
    }

    public void deleteNotification(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notificarea nu exista."));

        notificationRepository.delete(notification);
    }

    public Notification createInvitationNotification(Invitation invitation) {
        PostgresUser receiver = invitation.getReceiverId();
        PostgresUser sender = invitation.getSenderId();

        Notification notification = new Notification();
        notification.setUser(receiver);
        notification.setInvitation(invitation);
        notification.setTitle("Invitatie noua");
        notification.setMessage(sender.getName() + " te-a invitat la birou.");
        notification.setType("INVITATION");
        notification.setRead(false);
        notification.setCreatedAt(LocalDateTime.now());

        return notificationRepository.save(notification);
    }

    public Notification createColleaguesComingNotification(PostgresUser receiver, Reservation referenceReservation, String message) {
        Notification notification = new Notification();
        notification.setUser(receiver);
        notification.setReservation(referenceReservation);
        notification.setTitle("Your colleagues are coming to the office");
        notification.setMessage(message);
        notification.setType("COLLEAGUES_COMING");
        notification.setRead(false);
        notification.setCreatedAt(LocalDateTime.now());

        return notificationRepository.save(notification);
    }

    public boolean notificationExistsForUserAndReservation(Integer userId, Long reservationId) {
        if (userId == null || reservationId == null) {
            return false;
        }

        return notificationRepository.existsByUser_IdAndReservation_Id(userId, reservationId);
    }

    @Transactional
    public Reservation acceptColleaguesNotification(Long notificationId, PostgresUser accepter) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notificarea nu exista."));

        if (!notification.getUser().getId().equals(accepter.getId())) {
            throw new IllegalArgumentException("Notificarea nu apartine utilizatorului curent.");
        }

        Reservation referenceReservation = notification.getReservation();
        if (referenceReservation == null) {
            throw new IllegalArgumentException("Notificarea nu are o rezervare de referinta.");
        }

        Long roomId = null;
        if (referenceReservation.getRoom() != null) {
            roomId = referenceReservation.getRoom().getId();
        } else if (referenceReservation.getSeat() != null && referenceReservation.getSeat().getRoom() != null) {
            roomId = referenceReservation.getSeat().getRoom().getId();
        }

        if (roomId == null) {
            throw new IllegalArgumentException("Nu se poate determina sala pentru rezervarea de referinta.");
        }

        List<Seat> availableSeats = seatRepository.findAndLockAvailableSeatsInRoom(
                roomId,
                referenceReservation.getStartDateTime(),
                referenceReservation.getEndDateTime());
        if (availableSeats.isEmpty()) {
            throw new IllegalStateException("Nu exista locuri disponibile in aceeasi sala pentru intervalul respectiv.");
        }

        Seat reservedSeat = availableSeats.get(0);

        Reservation created = new Reservation(
                accepter,
                reservedSeat,
                null,
                referenceReservation.getStartDateTime(),
                referenceReservation.getEndDateTime(),
                Status.APPROVED,
                0);
        Reservation saved = reservationRepository.save(created);

        notification.setRead(true);
        notification.setReservation(saved);
        notificationRepository.save(notification);

        return saved;
    }

    public void markInvitationNotificationAsRead(Long invitationId) {
        notificationRepository.findByInvitation_Id(invitationId).ifPresent(notification -> {
            notification.setRead(true);
            notificationRepository.save(notification);
        });
    }

    public void deleteInvitationNotification(Long invitationId) {
        notificationRepository.findByInvitation_Id(invitationId)
                .ifPresent(notificationRepository::delete);
    }

    @Transactional
    public void deletePastInvitationNotifications() {
        notificationRepository.deletePastInvitationNotifications(LocalDateTime.now());
    }
}