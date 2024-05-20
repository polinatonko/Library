package com.example.library.repositories;

import com.example.library.models.Author;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface AuthorRepository extends JpaRepository<Author, Integer> {
}
