package com.example.library.repositories;

import com.example.library.models.Publisher;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PublisherRepository extends CrudRepository<Publisher, Integer> {
    Publisher findByName(String name);
    Publisher findByISBNPrefix(String ISBNPrefix);
    boolean existsByISBNPrefix(String ISBNPrefix);
}
