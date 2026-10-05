package com.itsmartsystems.bookyourseat.repository;

import com.itsmartsystems.bookyourseat.Status;
import com.itsmartsystems.bookyourseat.model.PostgresUser;
import com.itsmartsystems.bookyourseat.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByUser_Id(Integer userId);

    List<Reservation> findBySeat_Id(Long seatId);

    List<Reservation> findByRoom_Id(Long roomId);

    List<Reservation> findByStatus(Status status);

    List<Reservation> findByUser_IdAndStatus(Integer userId, Status status);

    List<Reservation> findBySeat_IdAndStatus(Long seatId, Status status);

    List<Reservation> findBySeat_IdAndStatusIn(Long seatId, Collection<Status> statuses);

    List<Reservation> findByRoom_IdAndStatus(Long roomId, Status status);

    List<Reservation> findByRoom_IdAndStatusIn(Long roomId, Collection<Status> statuses);

    List<Reservation> findByStartDateTimeBetween(LocalDateTime start, LocalDateTime end);

    List<Reservation> findByStatusAndReminderSentFalseAndStartDateTimeBetween(
            Status status,
            LocalDateTime start,
            LocalDateTime end);

    List<Reservation> findByEndDateTimeBetween(LocalDateTime start, LocalDateTime end);

    List<Reservation> findByStatusInAndStartDateTimeLessThanAndEndDateTimeGreaterThan(
            Collection<Status> statuses,
            LocalDateTime endDateTime,
            LocalDateTime startDateTime
    );

    List<Reservation> findBySeat_IdAndStatusInAndStartDateTimeLessThanAndEndDateTimeGreaterThan(
            Long seatId,
            Collection<Status> statuses,
            LocalDateTime endDateTime,
            LocalDateTime startDateTime
    );

    List<Reservation> findByUserDepartmentIdAndStartDateTimeBetween(
            Integer departmentId,
            LocalDateTime startOfDay,
            LocalDateTime endOfDay);

    boolean existsByUserAndStartDateTimeBetween(
            PostgresUser user,
            LocalDateTime startOfDay,
            LocalDateTime endOfDay);

    boolean existsByUserAndStatusInAndStartDateTimeLessThanAndEndDateTimeGreaterThan(
            PostgresUser user,
            Collection<Status> statuses,
            LocalDateTime endDateTime,
            LocalDateTime startDateTime);

    boolean existsBySeat_IdAndStatusInAndStartDateTimeLessThanAndEndDateTimeGreaterThan(
            Long seatId,
            Collection<Status> statuses,
            LocalDateTime endDateTime,
            LocalDateTime startDateTime);
}