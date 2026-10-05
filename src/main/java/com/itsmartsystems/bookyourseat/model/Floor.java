package com.itsmartsystems.bookyourseat.model;

import jakarta.persistence.*;

@Entity
@Table(name = "floor")
public class Floor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name" , nullable = false , length = 100)
    private String name ;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name)
    {
        this.name = name ;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Floor(String name) {
        this.name = name;
    }

    public Floor() {}


}
