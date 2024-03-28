package com.example.library.repositories;

import com.example.library.enums.ERole;
import com.example.library.models.User;
import com.example.library.tokens.VerificationToken;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public interface UserRepository extends CrudRepository<User, Integer> {
    User findUserByEmail(String email);
    User findUserByVerificationToken(VerificationToken verificationToken);
    boolean existsUserByEmail(String email);
    User findUserByName(String name);
    List<User> findByRoleName(ERole role);
}
