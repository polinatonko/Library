package com.example.library.repositories;

import com.example.library.models.Action;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActionRepository extends CrudRepository<Action, Integer> {
    Action findByUserId(Integer id);
}
