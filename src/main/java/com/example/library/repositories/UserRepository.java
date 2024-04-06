package com.example.library.repositories;

import com.example.library.enums.ERole;
import com.example.library.models.User;
import com.example.library.tokens.NewEmailToken;
import com.example.library.tokens.VerificationToken;
import jakarta.transaction.Transactional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface UserRepository extends CrudRepository<User, Integer> {
    Optional<User> findUserByEmail(String email);
    User findUserByVerificationToken(VerificationToken verificationToken);
    User findByNewEmailToken(NewEmailToken newEmailToken);
    boolean existsUserByEmail(String email);
    List<User> findByRoleName(ERole role);
}
