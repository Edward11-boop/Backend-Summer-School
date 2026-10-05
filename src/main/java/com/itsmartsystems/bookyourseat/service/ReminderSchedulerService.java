package com.itsmartsystems.bookyourseat.service;

import com.itsmartsystems.bookyourseat.Status;
import com.itsmartsystems.bookyourseat.model.Reservation;
import com.itsmartsystems.bookyourseat.repository.ReservationRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReminderSchedulerService {

    private final ReservationRepository reservationRepository;
    private final N8nService n8nService;

    public ReminderSchedulerService(ReservationRepository reservationRepository, N8nService n8nService) {
        this.reservationRepository = reservationRepository;
        this.n8nService = n8nService;
    }

    @Scheduled(fixedDelayString = "${reservation.reminder.check-delay-ms:60000}")
    @Transactional
    public void checkUpcomingReservations() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime in30Minutes = now.plusMinutes(30);

        List<Reservation> upcoming = reservationRepository
                .findByStatusAndReminderSentFalseAndStartDateTimeBetween(Status.APPROVED, now, in30Minutes);

        for (Reservation reservation : upcoming) {
            n8nService.sendReminderNotification(reservation);
            reservation.setReminderSent(true);
            reservationRepository.save(reservation);
        }
    }
}
