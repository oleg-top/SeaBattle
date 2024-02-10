package com.example.seabattle.repositories;

import com.example.seabattle.models.Prize;
import com.example.seabattle.models.Ship;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrizeRepository extends CrudRepository<Prize, Long> {
    List<Prize> findByUser_Username(String username);
    void deleteByShip(Ship ship);
}
