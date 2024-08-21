package com.example.library.repositories;

import com.example.library.models.Return;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface ReturnRepository extends CrudRepository<Return, Integer> {
    public Iterable<Return> findByUserId(Integer id);
    @Query("SELECT i FROM Return i WHERE i.day >= %?1% AND i.day <= %?2%")
    public List<Return> findByPeriod(Date from, Date to);
    @Query("SELECT i FROM Return i WHERE i.day >= %?1% AND i.day <= %?2% AND i.user.id = %?3%")
    public List<Return> findByPeriodAndUserId(Date from, Date to, Integer userId);
}
