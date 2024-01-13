package com.example.seabattle.services;

import com.example.seabattle.dtos.InviteUserResponse;
import com.example.seabattle.exceptions.AppError;
import com.example.seabattle.models.Field;
import com.example.seabattle.models.Shot;
import com.example.seabattle.models.User;
import com.example.seabattle.repositories.ShotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ShotService {

    private final ShotRepository shotRepository;

    public Optional<Shot> findById(Long id) {
        return shotRepository.findById(id);
    }

    public List<Shot> findByUser(User user) {
        return shotRepository.findByUser(user);
    }

    public Optional<Shot> findByUserAndField(User user, Field field) {
        return shotRepository.findByUserAndField(user, field);
    }

    public boolean createNewShot(Shot shot) {
        User user = shot.getUser();
        Field field = shot.getField();
        if (findByUserAndField(user, field).isPresent())
            return false;
        shotRepository.save(shot);
        return true;
    }

    public void delete(Shot shot) {
        shotRepository.delete(shot);
    }

    public void deleteShotByUserAndField(User user, Field field) {
        shotRepository.deleteShotByUserAndField(user, field);
    }

    public void updateAmount(Shot shot, Integer amount) {
        shot.setAmount(amount);
        shotRepository.save(shot);
    }

    public boolean correctAmount(Shot shot) {
        return shot.getAmount() != 0;
    }
}
