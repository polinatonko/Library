package com.example.library.repositories;

import com.example.library.models.Like;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikeRepository extends CrudRepository<Like, Integer> {
    Iterable<Like> findByUserId(Integer id);
    Optional<Like> findByUserIdAndEditionId(Integer userId, Integer editionId);
}
