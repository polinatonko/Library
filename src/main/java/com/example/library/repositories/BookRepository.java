package com.example.library.repositories;

import com.example.library.models.Book;
import com.example.library.models.Edition;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends CrudRepository<Edition, Integer> {

}
