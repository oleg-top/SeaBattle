package com.example.seabattle.dtos;

import lombok.Data;

@Data
public class TakeAShotRequest {
    private Long fieldId;
    private Integer x;
    private Integer y;
}
