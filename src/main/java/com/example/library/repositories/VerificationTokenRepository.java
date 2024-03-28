package com.example.library.repositories;

import com.example.library.tokens.VerificationToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VerificationTokenRepository extends CrudRepository<VerificationToken, Integer> {
    VerificationToken findByToken(String token);
}
