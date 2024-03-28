package com.example.library.repositories;

import com.example.library.tokens.PasswordResetToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PasswordResetTokenRepository extends CrudRepository<PasswordResetToken, Integer> {
    PasswordResetToken findByToken(String token);
}
