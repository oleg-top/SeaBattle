package com.example.seabattle.repositories;

import com.example.seabattle.models.Field;
import com.example.seabattle.models.Ship;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShipRepository extends CrudRepository<Ship, Long> {
    List<Ship> findByField(Field field);
}
