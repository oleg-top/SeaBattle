package com.example.seabattle.dtos;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class CreateShipRequest {
    private String name;
    private String description;
    private MultipartFile file;
}
