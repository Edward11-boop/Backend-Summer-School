package com.itsmartsystems.bookyourseat.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notification")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private PostgresUser user;

    @ManyToOne
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;

    @ManyToOne
    @JoinColumn(name = "invitation_id")
    private Invitation invitation;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Lob
    @Column(name = "message")
    private String message;

    @Column(name = "type", nullable = false, length = 50)
    private String type;

    @Column(name = "is_read", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean isRead;

    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    public Notification() {}

    public Notification(Long id, PostgresUser user, Reservation reservation, Invitation invitation, String title, String message, String type, boolean isRead, LocalDateTime createdAt) {
        this.id = id;
        this.user = user;
        this.reservation = reservation;
        this.invitation = invitation;
        this.title = title;
        this.message = message;
        this.type = type;
        this.isRead = isRead;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public PostgresUser getUser() { return user; }
    public Reservation getReservation() { return reservation; }
    public Invitation getInvitation() { return invitation; }
    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public String getType() { return type; }
    public boolean isRead() { return isRead; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setId(Long id) { this.id = id; }
    public void setUser(PostgresUser user) { this.user = user; }
    public void setReservation(Reservation reservation) { this.reservation = reservation; }
    public void setInvitation(Invitation invitation) { this.invitation = invitation; }
    public void setTitle(String title) { this.title = title; }
    public void setMessage(String message) { this.message = message; }
    public void setType(String type) { this.type = type; }
    public void setRead(boolean read) { isRead = read; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}