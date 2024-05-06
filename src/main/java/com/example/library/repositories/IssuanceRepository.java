package com.example.library.repositories;

import com.example.library.enums.IssuanceStatus;
import com.example.library.models.Issuance;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface IssuanceRepository extends CrudRepository<Issuance, Integer> {
    public Iterable<Issuance> findByUserId(Integer id);
    public Iterable<Issuance> findByStatus(IssuanceStatus status);
    //public Iterable<Issuance> findAllByIsActive(boolean isActive);
    @Query("SELECT i FROM Issuance i WHERE i.day >= %?1% AND i.day <= %?2%")
    public List<Issuance> findByPeriod(Date from, Date to);
    @Query("SELECT i FROM Issuance i WHERE i.day >= %?1% AND i.day <= %?2% AND i.user.id = %?3%")
    public List<Issuance> findByPeriodAndUserId(Date from, Date to, Integer userId);
    public boolean existsByUserIdAndEditionIdAndStatus(Integer userId, Integer editionId, IssuanceStatus status);
    //public boolean existsByUserIdAndEditionIdAndIsActive(Integer userId, Integer editionId, boolean isActive);
    public Issuance findByUserIdAndEditionIdAndStatus(Integer userId, Integer editionId, IssuanceStatus status);
    //public Issuance findByUserIdAndEditionIdAndIsActive(Integer userId, Integer editionId, boolean isActive);
}
