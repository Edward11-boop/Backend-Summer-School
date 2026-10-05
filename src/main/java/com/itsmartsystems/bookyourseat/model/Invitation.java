package com.itsmartsystems.bookyourseat.model;


import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "invitation")
public class Invitation {

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sender_id" , nullable = false)
    private PostgresUser senderId ;

    @ManyToOne
    @JoinColumn(name = "receiver_id" , nullable = false)
    private PostgresUser receiverId ;

    @ManyToOne
    @JoinColumn(name = "seat_id" , nullable = false)
    private Seat seatId;

    @Column(name = "start_date_time", nullable = false)
    private LocalDateTime startDateTime;

    @Column(name = "end_date_time", nullable = false)
    private LocalDateTime endDateTime;

    @Column(name = "status" , nullable = false , length = 50 , columnDefinition = "VARCHAR(50) DEFAULT 'PENDING'")
    private String status;

    @ManyToOne
    @JoinColumn(name = "created_reservation_id" , unique = true )
    private Reservation createdReservationId ;

    @Column(name = "created_at" , nullable = false , columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt ;

    @Column(name = "responded_at" )
    private LocalDateTime respondedAt ;

    public Invitation() {}

    public Invitation(Long id, PostgresUser senderId, PostgresUser receiverId, Seat seatId, LocalDateTime startDateTime, LocalDateTime endDateTime, String status, Reservation createdReservationId, LocalDateTime createdAt, LocalDateTime respondedAt) {
        this.id = id;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.seatId = seatId;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.status = status;
        this.createdReservationId = createdReservationId;
        this.createdAt = createdAt;
        this.respondedAt = respondedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PostgresUser getSenderId() {
        return senderId;
    }

    public void setSenderId(PostgresUser senderId) {
        this.senderId = senderId;
    }

    public PostgresUser getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(PostgresUser receiverId) {
        this.receiverId = receiverId;
    }

    public Seat getSeatId() {
        return seatId;
    }

    public void setSeatId(Seat seatId) {
        this.seatId = seatId;
    }

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(LocalDateTime startDateTime) {
        this.startDateTime = startDateTime;
    }

    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(LocalDateTime endDateTime) {
        this.endDateTime = endDateTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Reservation getCreatedReservationId() {
        return createdReservationId;
    }

    public void setCreatedReservationId(Reservation createdReservationId) {
        this.createdReservationId = createdReservationId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getRespondedAt() {
        return respondedAt;
    }

    public void setRespondedAt(LocalDateTime respondedAt) {
        this.respondedAt = respondedAt;
    }

}
