package com.example.library.repositories;

import com.example.library.models.Block;
import jakarta.transaction.Transactional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
public interface BlockRepository extends CrudRepository<Block, Integer> {
    Block findByUserIdAndIsActive(Integer userId, boolean isActive);
}
