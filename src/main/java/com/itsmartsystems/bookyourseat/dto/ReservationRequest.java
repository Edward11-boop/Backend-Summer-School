package com.itsmartsystems.bookyourseat.dto;

import java.time.LocalDateTime;

public class ReservationRequest {
    private String roomCode;
    private String seatCode;
    private LocalDateTime start;
    private LocalDateTime end;
    private Integer recurrence;

    public ReservationRequest() {
    }

    public String getRoomCode() {
        return roomCode;
    }

    public void setRoomCode(String roomCode) {
        this.roomCode = roomCode;
    }

    public String getSeatCode() {
        return seatCode;
    }

    public void setSeatCode(String seatCode) {
        this.seatCode = seatCode;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public void setEnd(LocalDateTime end) {
        this.end = end;
    }

    public Integer getRecurrence() {
        return recurrence;
    }

    public void setRecurrence(Integer recurrence) {
        this.recurrence = recurrence;
    }
}
