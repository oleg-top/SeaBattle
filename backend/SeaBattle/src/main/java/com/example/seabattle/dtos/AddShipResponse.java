package com.example.seabattle.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AddShipResponse {
    private String message;
    private Long id;
}
