package com.example.library.repositories;

import com.example.library.models.Issuance;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface IssuanceRepository extends CrudRepository<Issuance, Integer> {
    public Iterable<Issuance> findByUserId(Integer id);
    @Query("SELECT i FROM Issuance i WHERE i.day >= %?1% AND i.day <= %?2%")
    public List<Issuance> findByPeriod(Date from, Date to);
    @Query("SELECT i FROM Issuance i WHERE i.day >= %?1% AND i.day <= %?2% AND i.user.id = %?3%")
    public List<Issuance> findByPeriodAndUserId(Date from, Date to, Integer userId);
    public boolean existsByUserIdAndEditionIdAndIsActive(Integer userId, Integer editionId, boolean isActive);
    public Issuance findByUserIdAndEditionIdAndIsActive(Integer userId, Integer editionId, boolean isActive);
}
