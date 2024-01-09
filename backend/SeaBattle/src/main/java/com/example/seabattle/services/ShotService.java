package com.example.seabattle.services;

import com.example.seabattle.models.Field;
import com.example.seabattle.models.Shot;
import com.example.seabattle.models.User;
import com.example.seabattle.repositories.ShotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ShotService {

    private final ShotRepository shotRepository;

    public Optional<Shot> findById(Long id) {
        return shotRepository.findById(id);
    }

    public Optional<Shot> findByUserAndField(User user, Field field) {
        return shotRepository.findByUserAndField(user, field);
    }

    public void createNewShot(Shot shot) {
        shotRepository.save(shot);
    }

    public void deleteShotByUserAndField(User user, Field field) {
        shotRepository.deleteShotByUserAndField(user, field);
    }

    public boolean correctAmount(Shot shot) {
        return shot.getAmount() != 0;
    }
}
