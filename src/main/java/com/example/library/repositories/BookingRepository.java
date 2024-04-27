package com.example.library.repositories;

import com.example.library.models.Booking;
import com.example.library.models.Return;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends CrudRepository<Booking, Integer> {
    public Optional<Booking> findByEditionIdAndUserIdAndIsActive(Integer editionId, Integer userId, boolean isActive);
    Iterable<Booking> findByUserIdAndIsActive(Integer id, boolean isActive);
    @Query("SELECT i FROM Booking i WHERE i.day >= %?1% AND i.day <= %?2%")
    public List<Booking> findByPeriod(Date from, Date to);
    @Query("SELECT i FROM Booking i WHERE i.day >= %?1% AND i.day <= %?2% AND i.user.id = %?3%")
    public List<Booking> findByPeriodAndUserId(Date from, Date to, Integer userId);
}
