package com.example.seabattle.dtos;

import com.example.seabattle.models.Prize;
import com.example.seabattle.models.Shot;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class GetUserDataResponse {
    private Long id;
    private String username;
    private String role;
    private List<Prize> prizes;
    private List<Shot> shots;
}
