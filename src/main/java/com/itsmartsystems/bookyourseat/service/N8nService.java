package com.itsmartsystems.bookyourseat.service;

import com.itsmartsystems.bookyourseat.Status;
import com.itsmartsystems.bookyourseat.model.Invitation;
import com.itsmartsystems.bookyourseat.model.PostgresUser;
import com.itsmartsystems.bookyourseat.model.Reservation;
import com.itsmartsystems.bookyourseat.model.Room;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class N8nService {

    private static final Map<Status, String> STATUS_COLORS = Map.of(
            Status.APPROVED, "#24f928",
            Status.ACCEPTED, "#24f928",
            Status.PENDING, "#CA8A04",
            Status.REJECTED, "#DC2626",
            Status.DECLINED, "#DC2626");

    @Value("${n8n.reservation-verification.webhook.url:${n8n.webhook.url:}}")
    private String reservationWebhook;

    @Value("${n8n.reminder.webhook.url:}")
    private String reminderWebhook;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    @Value("${n8n.manager.approval.webhook.url:${N8N_MANAGER_APPROVAL_WEBHOOK_URL:}}")
    private String managerApprovalWebhook;

    @Value("${n8n.invitation.webhook.url:}")
    private String invitationWebhook;

    @Value("${n8n.invitation-response.webhook.url:${n8n.invitation.response.webhook.url:}}")
    private String invitationResponseWebhook;

    private final RestClient restClient;

    public N8nService(RestClient restClient) {
        this.restClient = restClient;
    }

    @Async
    public void sendReservationNotification(Reservation reservation) {
        postPayload(reservationWebhook, buildReservationPayload(reservation), "reservation");
    }

    @Async
    public void sendReminderNotification(Reservation reservation) {
        postPayload(reminderWebhook, buildReservationPayload(reservation), "reminder");
    }

    @Async
    public void sendManagerApprovalRequest(Reservation reservation, List<PostgresUser> approvers) {
        if (reservation == null) {
            System.err.println("Skipping manager approval notification: reservation is null.");
            return;
        }
        if (approvers == null || approvers.isEmpty()) {
            System.err.println("Skipping manager approval notification for reservation id=" + reservation.getId()
                    + " because no MANAGER/ADMIN approvers were found.");
            return;
        }

        Map<String, Object> payload = buildApprovalPayload(reservation, approvers);
        postPayload(managerApprovalWebhook, payload, "manager approval");
    }

    @Async
    public void sendRecurringReservationApprovalRequest(Reservation reservation, List<PostgresUser> approvers) {
        if (reservation == null) {
            System.err.println("Skipping recurring reservation approval notification: reservation is null.");
            return;
        }
        if (approvers == null || approvers.isEmpty()) {
            System.err.println("Skipping recurring reservation approval notification for reservation id=" + reservation.getId()
                    + " because no MANAGER/ADMIN approvers were found.");
            return;
        }

        Map<String, Object> payload = buildApprovalPayload(reservation, approvers);
        payload.put("isRecurring", true);
        payload.put("recurrenceWeeks", reservation.getRecurrence());
        postPayload(managerApprovalWebhook, payload, "manager approval");
    }

    @Async
    public void sendInvitationNotification(Invitation invitation) {
        Map<String, Object> payload = buildInvitationPayload(invitation);
        postPayload(invitationWebhook, payload, "invitation");
    }

    @Async
    public void sendInvitationResponseNotification(Invitation invitation) {
        Map<String, Object> payload = buildInvitationPayload(invitation);
        payload.put("senderEmail", invitation.getSenderId() == null ? null : invitation.getSenderId().getEmail());
        payload.put("receiverEmail", invitation.getReceiverId() == null ? null : invitation.getReceiverId().getEmail());
        postPayload(invitationResponseWebhook, payload, "invitation response");
    }

    private Map<String, Object> buildReservationPayload(Reservation reservation) {
        if (reservation == null) {
            return new HashMap<>();
        }

        Room room = reservation.getRoom();
        if (room == null && reservation.getSeat() != null) {
            room = reservation.getSeat().getRoom();
        }

        Map<String, Object> payload = new HashMap<>();
        payload.put("email", reservation.getUser() == null ? null : reservation.getUser().getEmail());
        payload.put("userName", reservation.getUser() == null ? null : reservation.getUser().getName());
        payload.put("userId", reservation.getUser() == null ? null : reservation.getUser().getId());
        payload.put("roomCode", room == null ? null : room.getCode());
        payload.put("roomName", room == null ? null : room.getName());
        payload.put("roomId", room == null ? null : room.getId());
        payload.put("seatCode", reservation.getSeat() == null ? null : reservation.getSeat().getCode());
        payload.put("seatId", reservation.getSeat() == null ? null : reservation.getSeat().getId());
        payload.put("startDateTime", reservation.getStartDateTime() == null ? null : reservation.getStartDateTime().toString());
        payload.put("endDateTime", reservation.getEndDateTime() == null ? null : reservation.getEndDateTime().toString());
        payload.put("statusLabel", reservation.getStatus() == null ? null : reservation.getStatus().name());
        payload.put("statusColor", reservation.getStatus() == null ? null : STATUS_COLORS.getOrDefault(reservation.getStatus(), "#24f928"));
        payload.put("status", reservation.getStatus() == null ? null : reservation.getStatus().name());
        return payload;
    }

    private Map<String, Object> buildApprovalPayload(Reservation reservation, List<PostgresUser> approvers) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("reservationId", reservation.getId());
        payload.put("userName", reservation.getUser() == null ? null : reservation.getUser().getName());
        payload.put("userEmail", reservation.getUser() == null ? null : reservation.getUser().getEmail());

        Room room = reservation.getRoom();
        if (room == null && reservation.getSeat() != null) {
            room = reservation.getSeat().getRoom();
        }
        payload.put("roomName", room == null ? null : room.getName());
        payload.put("roomCode", room == null ? null : room.getCode());
        payload.put("seatCode", reservation.getSeat() == null ? null : reservation.getSeat().getCode());
        payload.put("startDateTime", reservation.getStartDateTime() == null ? null : reservation.getStartDateTime().toString());
        payload.put("endDateTime", reservation.getEndDateTime() == null ? null : reservation.getEndDateTime().toString());

        String token = reservation.getApprovalToken();
        String approveUrl = token == null ? null : baseUrl + "/reservations/approve-link/" + reservation.getId() + "?token=" + token;
        String declineUrl = token == null ? null : baseUrl + "/reservations/decline-link/" + reservation.getId() + "?token=" + token;
        payload.put("approveUrl", approveUrl);
        payload.put("declineUrl", declineUrl);

        List<Map<String, String>> recipients = new java.util.ArrayList<>();
        if (approvers != null) {
            for (PostgresUser approver : approvers) {
                Map<String, String> recipient = new HashMap<>();
                recipient.put("email", approver.getEmail());
                recipient.put("name", approver.getName());
                recipients.add(recipient);
            }
        }
        payload.put("approvers", recipients);
        return payload;
    }

    private Map<String, Object> buildInvitationPayload(Invitation invitation) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("invitationId", invitation.getId());
        payload.put("status", invitation.getStatus());
        payload.put("senderName", invitation.getSenderId() == null ? null : invitation.getSenderId().getName());
        payload.put("senderEmail", invitation.getSenderId() == null ? null : invitation.getSenderId().getEmail());
        payload.put("receiverName", invitation.getReceiverId() == null ? null : invitation.getReceiverId().getName());
        payload.put("receiverEmail", invitation.getReceiverId() == null ? null : invitation.getReceiverId().getEmail());
        payload.put("seatCode", invitation.getSeatId() == null ? null : invitation.getSeatId().getCode());
        payload.put("roomCode", invitation.getSeatId() == null || invitation.getSeatId().getRoom() == null ? null : invitation.getSeatId().getRoom().getCode());
        payload.put("roomName", invitation.getSeatId() == null || invitation.getSeatId().getRoom() == null ? null : invitation.getSeatId().getRoom().getName());
        payload.put("startDateTime", invitation.getStartDateTime() == null ? null : invitation.getStartDateTime().toString());
        payload.put("endDateTime", invitation.getEndDateTime() == null ? null : invitation.getEndDateTime().toString());
        payload.put("respondedAt", invitation.getRespondedAt() == null ? null : invitation.getRespondedAt().toString());
        return payload;
    }

    private void postPayload(String webhook, Map<String, Object> payload, String type) {
        if (!StringUtils.hasText(webhook)) {
            System.err.println("Skipping n8n " + type + " notification because no webhook URL is configured.");
            return;
        }

        try {
            restClient.post()
                    .uri(webhook)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(payload)
                    .retrieve()
                    .toBodilessEntity();
            System.out.println("Successfully sent n8n " + type + " notification to " + webhook);
        } catch (Exception e) {
            System.err.println("Failed to send n8n " + type + " notification to " + webhook + ": " + e.getMessage());
        }
    }
}