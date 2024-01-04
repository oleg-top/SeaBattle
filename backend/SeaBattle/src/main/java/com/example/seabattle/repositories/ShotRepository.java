package com.example.seabattle.repositories;

import com.example.seabattle.models.Field;
import com.example.seabattle.models.Shot;
import com.example.seabattle.models.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShotRepository extends CrudRepository<Shot, Long> {
    List<Shot> findByUser(User user);
    List<Shot> findByField(Field field);
}
