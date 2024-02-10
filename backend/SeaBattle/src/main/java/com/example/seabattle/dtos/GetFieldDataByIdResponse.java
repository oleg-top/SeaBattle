package com.example.seabattle.dtos;

import com.example.seabattle.models.Field;
import com.example.seabattle.models.Ship;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class GetFieldDataByIdResponse {
    private Field field;
    private List<Ship> ships;
}
