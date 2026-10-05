package com.itsmartsystems.bookyourseat.model;


import jakarta.persistence.*;

@Entity
@Table(name =  "room")
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id ;

    @ManyToOne
    @JoinColumn(name = "floor_id" , nullable = false )
    private Floor floor;

    @Column(name = "code" ,unique = true ,  nullable = false , length = 50)
    private String code ;

    @Column(name = "name" , nullable = false , length = 100)
    private String name;

    @Column (name = "type" , nullable = false , length = 50)
    private String type ;

    public Long getId() {
        return id;
    }

    public Floor getFloor_id() {
        return floor;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public Long getCapacity() {
        return capacity;
    }


    public void setId(Long id) {
        this.id = id;
    }

    public void setFloor_id(Floor floor) {
        this.floor = floor;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setCapacity(Long capacity) {
        this.capacity = capacity;
    }

    @Column(name = "capacity" , nullable = false )
    private Long capacity ;

    public Room(Long id, Floor floor, String code, String name, String type, Long capacity) {
        this.id = id;
        this.floor = floor;
        this.code = code;
        this.name = name;
        this.type = type;
        this.capacity = capacity;
    }
    public Room() {}
    public Room(String code, String name, String level, String type, boolean wholeRoomOnly, int capacity, String status) {
        this.code = code;
        this.name = name;
        this.type = type;
        this.capacity = Long.valueOf(capacity);
        this.floor = new Floor(level);
    }



}
