package com.itsmartsystems.bookyourseat.dto;

import com.itsmartsystems.bookyourseat.model.Seat;

public record AvailableSeatResponse(Long id, String code, String type, String status, Long roomId) {
    public AvailableSeatResponse(Seat seat) {
        this(seat.getId(), seat.getCode(), seat.getType(), seat.getStatus(), seat.getSalaId());
    }
}