package com.itsmartsystems.bookyourseat.dto;

import com.itsmartsystems.bookyourseat.model.Floor;
import com.itsmartsystems.bookyourseat.model.Invitation;
import com.itsmartsystems.bookyourseat.model.Notification;
import com.itsmartsystems.bookyourseat.model.PostgresUser;
import com.itsmartsystems.bookyourseat.model.Reservation;
import com.itsmartsystems.bookyourseat.model.Room;
import com.itsmartsystems.bookyourseat.model.Seat;

import java.time.LocalDateTime;

public class NotificationResponse {

    private Long id;
    private String title;
    private String message;
    private String type;
    private boolean read;
    private LocalDateTime createdAt;
    private InvitationDetails invitation;
    private ReservationDetails reservation;

    public NotificationResponse(Notification notification) {
        this.id = notification.getId();
        this.title = notification.getTitle();
        this.message = notification.getMessage();
        this.type = notification.getType();
        this.read = notification.isRead();
        this.createdAt = notification.getCreatedAt();
        this.invitation = notification.getInvitation() == null ? null : new InvitationDetails(notification.getInvitation());
        this.reservation = notification.getReservation() == null ? null : new ReservationDetails(notification.getReservation());
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public String getType() { return type; }
    public boolean isRead() { return read; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public InvitationDetails getInvitation() { return invitation; }
    public ReservationDetails getReservation() { return reservation; }

    public static class InvitationDetails {
        private Long id;
        private UserDetails senderId;
        private UserDetails receiverId;
        private SeatDetails seatId;
        private LocalDateTime startDateTime;
        private LocalDateTime endDateTime;
        private String status;

        public InvitationDetails(Invitation invitation) {
            this.id = invitation.getId();
            this.senderId = new UserDetails(invitation.getSenderId());
            this.receiverId = new UserDetails(invitation.getReceiverId());
            this.seatId = new SeatDetails(invitation.getSeatId());
            this.startDateTime = invitation.getStartDateTime();
            this.endDateTime = invitation.getEndDateTime();
            this.status = invitation.getStatus();
        }

        public Long getId() { return id; }
        public UserDetails getSenderId() { return senderId; }
        public UserDetails getReceiverId() { return receiverId; }
        public SeatDetails getSeatId() { return seatId; }
        public LocalDateTime getStartDateTime() { return startDateTime; }
        public LocalDateTime getEndDateTime() { return endDateTime; }
        public String getStatus() { return status; }
    }

    public static class ReservationDetails {
        private Long id;
        private SeatDetails seat;
        private RoomDetails room;
        private LocalDateTime startDateTime;
        private LocalDateTime endDateTime;
        private String status;

        public ReservationDetails(Reservation reservation) {
            this.id = reservation.getId();
            this.seat = reservation.getSeat() == null ? null : new SeatDetails(reservation.getSeat());
            this.room = reservation.getRoom() == null ? null : new RoomDetails(reservation.getRoom());
            this.startDateTime = reservation.getStartDateTime();
            this.endDateTime = reservation.getEndDateTime();
            this.status = reservation.getStatus() == null ? null : reservation.getStatus().name();
        }

        public Long getId() { return id; }
        public SeatDetails getSeat() { return seat; }
        public RoomDetails getRoom() { return room; }
        public LocalDateTime getStartDateTime() { return startDateTime; }
        public LocalDateTime getEndDateTime() { return endDateTime; }
        public String getStatus() { return status; }
    }
    public static class UserDetails {
        private Integer id;
        private String name;
        private String email;

        public UserDetails(PostgresUser user) {
            this.id = user.getId();
            this.name = user.getName();
            this.email = user.getEmail();
        }

        public Integer getId() { return id; }
        public String getName() { return name; }
        public String getEmail() { return email; }
    }

    public static class SeatDetails {
        private Long id;
        private String code;
        private String status;
        private String type;
        private RoomDetails room;

        public SeatDetails(Seat seat) {
            this.id = seat.getId();
            this.code = seat.getCode();
            this.status = seat.getStatus();
            this.type = seat.getType();
            this.room = seat.getRoom() == null ? null : new RoomDetails(seat.getRoom());
        }

        public Long getId() { return id; }
        public String getCode() { return code; }
        public String getStatus() { return status; }
        public String getType() { return type; }
        public RoomDetails getRoom() { return room; }
    }

    public static class RoomDetails {
        private Long id;
        private String code;
        private String name;
        private String type;
        private FloorDetails floor_id;

        public RoomDetails(Room room) {
            this.id = room.getId();
            this.code = room.getCode();
            this.name = room.getName();
            this.type = room.getType();
            this.floor_id = room.getFloor_id() == null ? null : new FloorDetails(room.getFloor_id());
        }

        public Long getId() { return id; }
        public String getCode() { return code; }
        public String getName() { return name; }
        public String getType() { return type; }
        public FloorDetails getFloor_id() { return floor_id; }
    }

    public static class FloorDetails {
        private Long id;
        private String name;

        public FloorDetails(Floor floor) {
            this.id = floor.getId();
            this.name = floor.getName();
        }

        public Long getId() { return id; }
        public String getName() { return name; }
    }
}