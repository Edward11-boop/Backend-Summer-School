package com.itsmartsystems.bookyourseat.repository;

import com.itsmartsystems.bookyourseat.model.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    List<AuditLog> findByUserId(Long userId);

    List<AuditLog> findByReservationId(Long reservationId);

    List<AuditLog> findByAction(String action);

    List<AuditLog> findBySource(String source);

    List<AuditLog> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    List<AuditLog> findAllByOrderByCreatedAtDesc();
}