package com.example.seabattle.services;

import com.example.seabattle.models.Shot;
import com.example.seabattle.repositories.ShotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShotService {

    private final ShotRepository shotRepository;

    public void createNewShot(Shot shot) {
        shotRepository.save(shot);
    }

    public boolean checkAmount(Shot shot) {
        return shot.getAmount() != 0;
    }
}
