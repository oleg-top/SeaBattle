package com.example.seabattle.dtos;

import lombok.Data;

@Data
public class AddShipRequest {
    private String name;
    private String description;
    private Integer x;
    private Integer y;
    private Long fieldId;
}
