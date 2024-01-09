package com.example.seabattle.dtos;

import com.example.seabattle.models.Prize;
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
}
