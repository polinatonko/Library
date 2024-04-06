package com.example.library.services;

import com.example.library.enums.ERole;
import com.example.library.models.Role;
import com.example.library.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleService {
    @Autowired
    private RoleRepository roleRepository;

    public Role getRoleByName(ERole name)
    {
        Role role = roleRepository.findByName(name);
        return role == null ? roleRepository.save(new Role(name)) : role;
    }
}
