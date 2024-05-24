package com.example.library.repositories;

import com.example.library.models.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Integer>, JpaSpecificationExecutor<Book> {
    public Iterable<Book> findByGenresId(Integer id);
    public Iterable<Book> findByReceiptDateGreaterThan(Date date);
    Page<Book> findByGenresId(Integer id, Pageable pageable);
    Page<Book> findByPublisherId(Integer id, Pageable pageable);
}
