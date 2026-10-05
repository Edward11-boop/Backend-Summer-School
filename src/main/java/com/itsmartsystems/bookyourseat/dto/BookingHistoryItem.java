package com.itsmartsystems.bookyourseat.dto;

import java.time.LocalDateTime;

public record BookingHistoryItem(
        Long id,
        LocalDateTime date,
        String seat,
        String room,
        String status
) {
}
