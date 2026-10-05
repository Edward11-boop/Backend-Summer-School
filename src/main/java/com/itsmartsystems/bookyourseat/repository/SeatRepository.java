package com.itsmartsystems.bookyourseat.repository;

import com.itsmartsystems.bookyourseat.model.Seat;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SeatRepository extends JpaRepository<Seat, Long> {

    Optional<Seat> findByCode(String code);

    default Seat findByCod(String code) {
        return findByCode(code).orElse(null);
    }

    default Optional<Seat> findById(String id) {
        return findById(Long.valueOf(id));
    }

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from Seat s where s.code = :code")
    Optional<Seat> findByCodeForUpdate(@Param("code") String code);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from Seat s where s.id = :id")
    Optional<Seat> findByIdForUpdate(@Param("id") Long id);

    List<Seat> findByRoomId(Long roomId);

    default List<Seat> findBySalaId(String roomId) {
        return findByRoomId(Long.valueOf(roomId));
    }

    List<Seat> findByStatus(String status);

    List<Seat> findByType(String type);

    List<Seat> findByRoomIdAndStatus(Long roomId, String status);

    boolean existsByCodeAndRoomId(String code, Long roomId);

    @Query("""
        select s from Seat s
        where s.room.id = :roomId
        and upper(s.status) in ('AVAILABLE', 'ACTIVE')
        and s.id not in (
            select r.seat.id from Reservation r
            where r.seat is not null
            and r.seat.room.id = :roomId
            and r.status in (com.itsmartsystems.bookyourseat.Status.APPROVED, com.itsmartsystems.bookyourseat.Status.ACCEPTED, com.itsmartsystems.bookyourseat.Status.PENDING)
            and r.startDateTime < :endDateTime
            and r.endDateTime > :startDateTime
        )
        and not exists (
            select 1 from Reservation roomReservation
            where roomReservation.room is not null
            and roomReservation.room.id = :roomId
            and roomReservation.status in (com.itsmartsystems.bookyourseat.Status.APPROVED, com.itsmartsystems.bookyourseat.Status.ACCEPTED, com.itsmartsystems.bookyourseat.Status.PENDING)
            and roomReservation.startDateTime < :endDateTime
            and roomReservation.endDateTime > :startDateTime
        )
    """)
    List<Seat> findAvailableSeatsInRoom(
            @Param("roomId") Long roomId,
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        select s from Seat s
        where s.room.id = :roomId
        and upper(s.status) in ('AVAILABLE', 'ACTIVE')
        and s.id not in (
            select r.seat.id from Reservation r
            where r.seat is not null
            and r.seat.room.id = :roomId
            and r.status in (com.itsmartsystems.bookyourseat.Status.APPROVED, com.itsmartsystems.bookyourseat.Status.ACCEPTED, com.itsmartsystems.bookyourseat.Status.PENDING)
            and r.startDateTime < :endDateTime
            and r.endDateTime > :startDateTime
        )
        and not exists (
            select 1 from Reservation roomReservation
            where roomReservation.room is not null
            and roomReservation.room.id = :roomId
            and roomReservation.status in (com.itsmartsystems.bookyourseat.Status.APPROVED, com.itsmartsystems.bookyourseat.Status.ACCEPTED, com.itsmartsystems.bookyourseat.Status.PENDING)
            and roomReservation.startDateTime < :endDateTime
            and roomReservation.endDateTime > :startDateTime
        )
    """)
    List<Seat> findAndLockAvailableSeatsInRoom(
            @Param("roomId") Long roomId,
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime);
}
