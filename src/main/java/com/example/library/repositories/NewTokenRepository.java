package com.example.library.repositories;

import com.example.library.tokens.NewEmailToken;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface NewTokenRepository extends CrudRepository<NewEmailToken, Integer> {
    Optional<NewEmailToken> findByToken(String token);
}
