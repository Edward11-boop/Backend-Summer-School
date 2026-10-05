package com.itsmartsystems.bookyourseat.dto;

import com.itsmartsystems.bookyourseat.model.Reservation;
import com.itsmartsystems.bookyourseat.model.Room;
import com.itsmartsystems.bookyourseat.model.Seat;

import java.time.LocalDateTime;

public class ReservationResponse {

    private Long id;
    private String status;
    private String userName;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private SeatDetails seat;
    private RoomDetails room;

    public ReservationResponse(Reservation reservation) {
        this.id = reservation.getId();
        this.status = reservation.getStatus() == null ? null : reservation.getStatus().name();
        this.userName = reservation.getUser() == null ? null : reservation.getUser().getName();
        this.startDateTime = reservation.getStartDateTime();
        this.endDateTime = reservation.getEndDateTime();
        this.seat = reservation.getSeat() == null ? null : new SeatDetails(reservation.getSeat());
        this.room = reservation.getRoom() == null ? null : new RoomDetails(reservation.getRoom());
    }

    public Long getId() { return id; }
    public String getStatus() { return status; }
    public String getUserName() { return userName; }
    public LocalDateTime getStartDateTime() { return startDateTime; }
    public LocalDateTime getEndDateTime() { return endDateTime; }
    public SeatDetails getSeat() { return seat; }
    public RoomDetails getRoom() { return room; }

    public static class SeatDetails {
        private Long id;
        private String code;
        private String status;

        public SeatDetails(Seat seat) {
            this.id = seat.getId();
            this.code = seat.getCode();
            this.status = seat.getStatus();
        }

        public Long getId() { return id; }
        public String getCode() { return code; }
        public String getStatus() { return status; }
    }

    public static class RoomDetails {
        private Long id;
        private String code;
        private String name;

        public RoomDetails(Room room) {
            this.id = room.getId();
            this.code = room.getCode();
            this.name = room.getName();
        }

        public Long getId() { return id; }
        public String getCode() { return code; }
        public String getName() { return name; }
    }
}