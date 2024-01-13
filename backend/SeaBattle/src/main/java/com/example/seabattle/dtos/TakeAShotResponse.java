package com.example.seabattle.dtos;

import com.example.seabattle.models.Prize;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TakeAShotResponse {
    private String status;
    private Prize prize;
}
