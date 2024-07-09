package com.example.library.repositories;

import com.example.library.models.Booking;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends CrudRepository<Booking, Integer> {
    @Query(value = "SELECT * FROM ACTION WHERE ACTION_TYPE = 'BOOK' AND EDITION_ID = ?1 AND USER_ID = ?2 AND IS_ACTIVE = ?3",
            nativeQuery = true)
    Optional<Booking> findByEdition_IdAndUser_IdAndIsActive(Integer editionId, Integer userId, boolean isActive);
    Iterable<Booking> findByUserIdAndIsActive(Integer id, boolean isActive);
    @Query("select t from Booking t join t.user c where c.id = :id")
    Iterable<Booking> findByUserId(Integer id);
    @Query("SELECT i FROM Booking i WHERE i.day >= %?1% AND i.day <= %?2%")
    public List<Booking> findByPeriod(Date from, Date to);
    @Query("SELECT i FROM Booking i WHERE i.day >= %?1% AND i.day <= %?2% AND i.user.id = %?3%")
    public List<Booking> findByPeriodAndUserId(Date from, Date to, Integer userId);
}

