package com.example.seabattle.services;

import com.example.seabattle.models.Role;
import com.example.seabattle.repositories.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;

    Optional<Role> findByName(String name) {
        return roleRepository.findByName(name);
    }
}
