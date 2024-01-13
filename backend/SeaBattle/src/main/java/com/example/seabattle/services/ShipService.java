package com.example.seabattle.services;

import com.example.seabattle.models.Field;
import com.example.seabattle.models.Ship;
import com.example.seabattle.repositories.ShipRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ShipService {

    private final ShipRepository shipRepository;
    private final PrizeService prizeService;

    public List<Ship> findByField(Field field) {
        return shipRepository.findByField(field);
    }

    public Optional<Ship> findById(Long id) {
        return shipRepository.findById(id);
    }

    public Optional<Ship> findByCoordinatesOnField(Integer x, Integer y, Field field) {
        return shipRepository.findByXAndYAndField(x, y, field);
    }

    public Boolean isActive(Ship ship) {
        return ship.getActive();
    }

    public void deactivateShip(Ship ship) {
        ship.setActive(false);
        shipRepository.save(ship);
    }

    public void createNewShip(Ship ship) {
        ship.setActive(true);
        shipRepository.save(ship);
    }

    @Transactional
    public void deleteShip(Ship ship) {
        prizeService.deletePrizeByShip(ship);
        shipRepository.delete(ship);
    }
}
