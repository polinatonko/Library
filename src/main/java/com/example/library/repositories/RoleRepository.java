package com.example.library.repositories;

import com.example.library.models.Role;
import com.example.library.enums.ERole;
import jakarta.transaction.Transactional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
public interface RoleRepository extends CrudRepository<Role, Integer> {
    boolean existsByName(ERole name);
    Role findByName(ERole name);
}
