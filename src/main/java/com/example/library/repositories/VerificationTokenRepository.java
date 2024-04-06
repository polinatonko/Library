package com.example.library.repositories;

import com.example.library.tokens.VerificationToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VerificationTokenRepository extends CrudRepository<VerificationToken, Integer> {
    Optional<VerificationToken> findByToken(String token);
}
