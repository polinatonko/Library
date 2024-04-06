package com.example.library.repositories;

import com.example.library.tokens.PasswordResetToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends CrudRepository<PasswordResetToken, Integer> {
    Optional<PasswordResetToken> findByToken(String token);
}
