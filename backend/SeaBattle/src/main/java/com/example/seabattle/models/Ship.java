package com.example.seabattle.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "ships")
public class Ship {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String image;

    private String name;

    private String description;

    @ManyToOne
    private Field field;

    private Integer x;

    private Integer y;

    private Boolean active;

    private Boolean empty;
}
