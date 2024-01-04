package com.example.seabattle.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "shots")
public class Shot {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Integer amount;

    @ManyToOne
    private User user;

    @ManyToOne
    private Field field;
}
