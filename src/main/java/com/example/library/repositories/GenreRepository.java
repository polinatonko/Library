package com.example.library.repositories;

import com.example.library.models.Genre;
import jakarta.transaction.Transactional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
public interface GenreRepository extends CrudRepository<Genre, Integer> {
    public void deleteByName(String name);
}
