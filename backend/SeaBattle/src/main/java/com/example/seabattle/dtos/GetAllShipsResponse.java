package com.example.seabattle.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class GetAllShipsResponse {
    List<String> names;
    List<String> descriptions;
    List<Long> ids;
}
