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

    private String description;

    @ManyToOne
    private Field field;

    private Integer pos_x;

    private Integer pos_y;
}
