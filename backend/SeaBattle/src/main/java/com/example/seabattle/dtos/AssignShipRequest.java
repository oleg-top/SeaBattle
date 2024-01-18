package com.example.seabattle.dtos;

import lombok.Data;

@Data
public class AssignShipRequest {
    private Long shipId;
    private Long fieldId;
    private Integer x;
    private Integer y;
}
