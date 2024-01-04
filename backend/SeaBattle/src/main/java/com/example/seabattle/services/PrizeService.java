package com.example.seabattle.services;

import com.example.seabattle.models.Prize;
import com.example.seabattle.repositories.PrizeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PrizeService {

    private final PrizeRepository prizeRepository;

    public void createNewPrize(Prize prize) {
        prizeRepository.save(prize);
    }

    public Optional<Prize> findById(Long id) {
        return prizeRepository.findById(id);
    }
}
