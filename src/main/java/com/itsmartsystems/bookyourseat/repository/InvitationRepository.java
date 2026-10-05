package com.itsmartsystems.bookyourseat.repository;

import com.itsmartsystems.bookyourseat.model.Invitation;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface InvitationRepository extends JpaRepository<Invitation, Long> {

    List<Invitation> findBySenderId_Id(Integer senderId);

    List<Invitation> findByReceiverId_Id(Integer receiverId);

    List<Invitation> findBySeatId_Id(Long seatId);

    List<Invitation> findByStatus(String status);

    List<Invitation> findByReceiverId_IdAndStatus(Integer receiverId, String status);

    List<Invitation> findBySeatId_IdAndStatus(Long seatId, String status);

    List<Invitation> findBySeatId_IdAndStatusAndStartDateTimeLessThanAndEndDateTimeGreaterThan(
            Long seatId, String status, LocalDateTime endDateTime, LocalDateTime startDateTime);

    List<Invitation> findByStatusAndStartDateTimeLessThanEqual(String status, LocalDateTime startDateTime);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @org.springframework.data.jpa.repository.Query("select i from Invitation i where i.id = :id")
    Optional<Invitation> findByIdForUpdate(
            @org.springframework.data.repository.query.Param("id") Long id);

    boolean existsByCreatedReservationId_Id(Long reservationId);
}