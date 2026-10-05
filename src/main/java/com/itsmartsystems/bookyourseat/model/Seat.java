package com.itsmartsystems.bookyourseat.model;

import jakarta.persistence.*;

@Entity
@Table(name = "seat")
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @Column(name = "code", nullable = false, length = 50)
    private String code;

    @Column(name = "status", nullable = false, length = 50)
    private String status;

    @Column(name = "type", nullable = false, length = 50)
    private String type;

    public Seat() {}
    public Seat(Long roomId, String code, String status) {
        Room room = new Room();
        room.setId(roomId);
        this.room = room;
        this.code = code;
        this.status = status;
        this.type = "DESK";
    }

    public Seat(Long id, Room room, String code, String status, String type) {
        this.id = id;
        this.room = room;
        this.code = code;
        this.status = status;
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSalaId() { return room == null ? null : room.getId(); }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}