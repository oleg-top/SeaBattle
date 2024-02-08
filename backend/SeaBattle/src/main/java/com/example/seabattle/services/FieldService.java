package com.example.seabattle.services;

import com.example.seabattle.models.Field;
import com.example.seabattle.repositories.FieldRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FieldService {

    private final FieldRepository fieldRepository;
    private final ShipService shipService;

    public Field createNewField(Field field) {
        fieldRepository.save(field);
        return field;
    }

    public Optional<Field> findById(Long id) {
        return fieldRepository.findById(id);
    }
}
