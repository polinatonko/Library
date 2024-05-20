package com.example.library.repositories;

import com.example.library.models.Book;
import com.example.library.models.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer>, JpaSpecificationExecutor<Review> {
    public boolean existsByUserIdAndEditionId(Integer userId, Integer editionId);
    public Iterable<Review> findByEditionId(Integer id);
    public Iterable<Review> findByUserId(Integer id);
    @Transactional
    public Long deleteByEditionIdAndUserId(Integer editionId, Integer userId);
}
