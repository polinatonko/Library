package com.example.library.repositories;

import com.example.library.models.Block;
import com.example.library.models.Issuance;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
@Transactional
public interface BlockRepository extends CrudRepository<Block, Integer> {
    Block findByUserIdAndIsActive(Integer userId, boolean isActive);
    Iterable<Block> findAllByIsActive(boolean isActive);
    @Query("SELECT i FROM Block i WHERE i.day >= %?1% AND i.day <= %?2%")
    public List<Block> findByPeriod(Date from, Date to);
    @Query("SELECT i FROM Block i WHERE i.day >= %?1% AND i.day <= %?2% AND i.user.id = %?3%")
    public List<Block> findByPeriodAndUserId(Date from, Date to, Integer userId);
}

