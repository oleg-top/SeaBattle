package com.example.seabattle.services;

import com.example.seabattle.models.Field;
import com.example.seabattle.models.Ship;
import com.example.seabattle.repositories.ShipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ShipService {

    private final ShipRepository shipRepository;

    public List<Ship> findByField(Field field) {
        return shipRepository.findByField(field);
    }

    public Optional<Ship> findById(Long id) {
        return shipRepository.findById(id);
    }

    public Optional<Ship> findByCoordinatesOnField(Integer x, Integer y, Field field) {
        return shipRepository.findByXAndYAndField(x, y, field);
    }

    public void createNewShip(Ship ship) {
        shipRepository.save(ship);
    }
}
