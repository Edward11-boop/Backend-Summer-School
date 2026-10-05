package com.itsmartsystems.bookyourseat.service;

import com.itsmartsystems.bookyourseat.Status;
import com.itsmartsystems.bookyourseat.dto.ColleagueDto;
import com.itsmartsystems.bookyourseat.dto.InvitationRequest;
import com.itsmartsystems.bookyourseat.model.Invitation;
import com.itsmartsystems.bookyourseat.model.PostgresUser;
import com.itsmartsystems.bookyourseat.model.Reservation;
import com.itsmartsystems.bookyourseat.model.Seat;
import com.itsmartsystems.bookyourseat.repository.InvitationRepository;
import com.itsmartsystems.bookyourseat.repository.PostgresUserRepository;
import com.itsmartsystems.bookyourseat.repository.ReservationRepository;
import com.itsmartsystems.bookyourseat.repository.SeatRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class InvitationService {

    private static final String INVITATION_PENDING = "PENDING";
    private static final String INVITATION_ACCEPTED = "ACCEPTED";
    private static final String INVITATION_DECLINED = "DECLINED";
    private static final List<Status> ACTIVE_RESERVATION_STATUSES = List.of(
            Status.APPROVED,
            Status.ACCEPTED,
            Status.PENDING);

    private final InvitationRepository invitationRepository;
    private final PostgresUserRepository postgresUserRepository;
    private final SeatRepository seatRepository;
    private final ReservationRepository reservationRepository;
    private final NotificationService notificationService;
    private final N8nService n8nService;

    public InvitationService(InvitationRepository invitationRepository, PostgresUserRepository postgresUserRepository,
            SeatRepository seatRepository, ReservationRepository reservationRepository,
            NotificationService notificationService, N8nService n8nService) {
        this.invitationRepository = invitationRepository;
        this.postgresUserRepository = postgresUserRepository;
        this.seatRepository = seatRepository;
        this.reservationRepository = reservationRepository;
        this.notificationService = notificationService;
        this.n8nService = n8nService;
    }

    @Transactional
    public Invitation createInvitation(InvitationRequest request) {
        if (request.getSenderId() == null) {
            throw new IllegalArgumentException("Expeditorul invitatiei lipseste.");
        }

        PostgresUser sender = postgresUserRepository.findById(request.getSenderId())
                .orElseThrow(() -> new RuntimeException("Expeditorul nu a fost gasit in baza de date Postgres."));

        return createInvitation(sender, request);
    }

    @Transactional
    public Invitation createInvitation(PostgresUser sender, InvitationRequest request) {
        validateInvitationInterval(request);

        PostgresUser receiver = postgresUserRepository.findById(request.getReceiverId())
                .orElseThrow(() -> new RuntimeException("Destinatarul nu a fost gasit in baza de date Postgres."));

        if (sender.getId().equals(receiver.getId())) {
            throw new IllegalArgumentException("Nu te poti invita pe tine insuti.");
        }

        validateReceiverEligibility(sender, receiver, request.getStartDateTime().toLocalDate());

        Seat seat = seatRepository.findByIdForUpdate(request.getSeatId())
                .orElseThrow(() -> new RuntimeException("Scaunul nu a fost gasit."));

        ensureSeatIsAvailable(seat.getId(), request.getStartDateTime(), request.getEndDateTime());

        Reservation pendingReservation = new Reservation(
                receiver,
                seat,
                null,
                request.getStartDateTime(),
                request.getEndDateTime(),
                Status.PENDING,
                0);

        Invitation invitation = new Invitation();
        invitation.setSenderId(sender);
        invitation.setReceiverId(receiver);
        invitation.setSeatId(seat);
        invitation.setStartDateTime(request.getStartDateTime());
        invitation.setEndDateTime(request.getEndDateTime());
        invitation.setStatus(INVITATION_PENDING);
        invitation.setCreatedAt(LocalDateTime.now());
        invitation.setCreatedReservationId(reservationRepository.save(pendingReservation));

        Invitation savedInvitation = invitationRepository.save(invitation);
        notificationService.createInvitationNotification(savedInvitation);
        n8nService.sendInvitationNotification(savedInvitation);

        return savedInvitation;
    }

    public List<ColleagueDto> getEligibleColleagues(PostgresUser currentUser, LocalDate date) {
        return postgresUserRepository.findByDepartmentId(currentUser.getDepartmentId()).stream()
                .filter(user -> !user.getId().equals(currentUser.getId()))
                .filter(user -> date == null || !hasReservationOnDate(user, date))
                .map(user -> new ColleagueDto(user.getId(), user.getName(), user.getEmail()))
                .toList();
    }

    public List<Invitation> getPendingInvitationsForUser(Integer userId) {
        return invitationRepository.findByReceiverId_IdAndStatus(userId, INVITATION_PENDING);
    }

    @Transactional
    public void acceptInvitation(Long invitationId) {
        acceptInvitation(invitationId, null);
    }

    @Transactional
    public void acceptInvitation(Long invitationId, Integer currentUserId) {
        Invitation invitation = invitationRepository.findByIdForUpdate(invitationId)
                .orElseThrow(() -> new RuntimeException("Invitatia nu exista."));

        validatePendingInvitation(invitation, currentUserId);

        Reservation savedReservation = invitation.getCreatedReservationId();
        if (savedReservation != null) {
            if (savedReservation.getStatus() != Status.PENDING) {
                throw new RuntimeException("Rezervarea temporara a invitatiei nu mai este disponibila.");
            }

            savedReservation.setStatus(Status.APPROVED);
            savedReservation = reservationRepository.save(savedReservation);
        } else {
            Seat invitedSeat = seatRepository.findByIdForUpdate(invitation.getSeatId().getId())
                    .orElseThrow(() -> new RuntimeException("Locul invitat nu mai exista."));
            ensureSeatIsAvailable(invitedSeat.getId(), invitation.getStartDateTime(), invitation.getEndDateTime());

            Reservation reservation = new Reservation();
            reservation.setUser(invitation.getReceiverId());
            reservation.setSeat(invitedSeat);
            reservation.setStartDateTime(invitation.getStartDateTime());
            reservation.setEndDateTime(invitation.getEndDateTime());
            reservation.setStatus(Status.APPROVED);
            reservation.setRecurrence(0);
            savedReservation = reservationRepository.save(reservation);
        }

        invitation.setStatus(INVITATION_ACCEPTED);
        invitation.setRespondedAt(LocalDateTime.now());
        invitation.setCreatedReservationId(savedReservation);

        invitationRepository.save(invitation);
        notificationService.markInvitationNotificationAsRead(invitationId);
        n8nService.sendInvitationResponseNotification(invitation);
    }

    @Transactional
    public void declineInvitation(Long invitationId) {
        declineInvitation(invitationId, null);
    }

    @Transactional
    public void declineInvitation(Long invitationId, Integer currentUserId) {
        Invitation invitation = invitationRepository.findByIdForUpdate(invitationId)
                .orElseThrow(() -> new RuntimeException("Invitatia nu exista."));

        validatePendingInvitation(invitation, currentUserId);
        declinePendingInvitation(invitation);
        notificationService.markInvitationNotificationAsRead(invitationId);
    }

    @Scheduled(fixedDelay = 60000)
    @Transactional
    public void expireInvitations() {
        invitationRepository.findByStatusAndStartDateTimeLessThanEqual(INVITATION_PENDING, LocalDateTime.now())
                .forEach(invitation -> {
                    declinePendingInvitation(invitation);
                    notificationService.markInvitationNotificationAsRead(invitation.getId());
                });
    }

    private void validatePendingInvitation(Invitation invitation, Integer currentUserId) {
        if (!INVITATION_PENDING.equals(invitation.getStatus())) {
            throw new RuntimeException("Invitatia a fost deja procesata.");
        }

        if (currentUserId != null && !invitation.getReceiverId().getId().equals(currentUserId)) {
            throw new IllegalArgumentException("Aceasta invitatie nu iti apartine.");
        }

        if (!invitation.getStartDateTime().isAfter(LocalDateTime.now())) {
            declinePendingInvitation(invitation);
            throw new RuntimeException("Invitatia a expirat.");
        }
    }

    private void declinePendingInvitation(Invitation invitation) {
        invitation.setStatus(INVITATION_DECLINED);
        invitation.setRespondedAt(LocalDateTime.now());

        Reservation pendingReservation = invitation.getCreatedReservationId();
        if (pendingReservation != null && pendingReservation.getStatus() == Status.PENDING) {
            pendingReservation.setStatus(Status.REJECTED);
            reservationRepository.save(pendingReservation);
        }

        invitationRepository.save(invitation);
        n8nService.sendInvitationResponseNotification(invitation);
    }

    private void validateInvitationInterval(InvitationRequest request) {
        if (request.getReceiverId() == null || request.getSeatId() == null
                || request.getStartDateTime() == null || request.getEndDateTime() == null) {
            throw new IllegalArgumentException("Datele invitatiei sunt incomplete.");
        }

        if (!request.getEndDateTime().isAfter(request.getStartDateTime())) {
            throw new IllegalArgumentException("Ora de final trebuie sa fie dupa ora de inceput.");
        }
    }

    private void validateReceiverEligibility(PostgresUser sender, PostgresUser receiver, LocalDate invitationDate) {
        if (!Objects.equals(sender.getDepartmentId(), receiver.getDepartmentId())) {
            throw new IllegalArgumentException("Poti invita doar colegi din departamentul tau.");
        }

        if (hasReservationOnDate(receiver, invitationDate)) {
            throw new IllegalArgumentException("Colegul selectat are deja o rezervare in ziua respectiva.");
        }
    }

    private boolean hasReservationOnDate(PostgresUser user, LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();

        return reservationRepository.existsByUserAndStatusInAndStartDateTimeLessThanAndEndDateTimeGreaterThan(
                user,
                ACTIVE_RESERVATION_STATUSES,
                endOfDay,
                startOfDay);
    }

    private void ensureSeatIsAvailable(Long seatId, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        boolean isReserved = !reservationRepository
                .findBySeat_IdAndStatusInAndStartDateTimeLessThanAndEndDateTimeGreaterThan(
                        seatId,
                        ACTIVE_RESERVATION_STATUSES,
                        endDateTime,
                        startDateTime)
                .isEmpty();

        if (isReserved) {
            throw new IllegalStateException("Locul nu mai este disponibil pentru intervalul selectat.");
        }
    }
}
