package com.itsmartsystems.bookyourseat.dto;

import java.util.List;

public record BookingSummaryResponse(BookingStats bookingStats, List<BookingHistoryItem> bookingHistory) {
}
