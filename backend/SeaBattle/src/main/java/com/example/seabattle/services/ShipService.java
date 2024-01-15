package com.example.seabattle.services;

import com.example.seabattle.models.Field;
import com.example.seabattle.models.Ship;
import com.example.seabattle.repositories.ShipRepository;
import com.example.seabattle.utils.FileStorageUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ShipService {

    private final ShipRepository shipRepository;
    private final PrizeService prizeService;
    private final FileStorageUtils fileStorageUtils;

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

    public void createNewShip(Ship ship, MultipartFile file) {
        ship.setActive(true);
        shipRepository.save(ship);
        String filename = ship.getId() + "_" + file.getOriginalFilename();
        ship.setImage(filename);
        shipRepository.save(ship);
        fileStorageUtils.save(file, filename);
    }

    @Transactional
    public void deleteShip(Ship ship) {
        prizeService.deletePrizeByShip(ship);
        shipRepository.delete(ship);
        fileStorageUtils.delete(ship.getImage());
    }
}
