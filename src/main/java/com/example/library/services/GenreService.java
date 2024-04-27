package com.example.library.services;

import com.example.library.models.Book;
import com.example.library.models.Genre;
import com.example.library.repositories.BookRepository;
import com.example.library.repositories.GenreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GenreService {
    @Autowired
    private GenreRepository genreRepository;
    @Autowired
    private BookRepository bookRepository;
    public void save(Genre genre) { genreRepository.save(genre); }

    public Genre getById(Integer id) { return getByIdOrThrowException(id); }

    public Iterable<Genre> getAll() { return genreRepository.findAll(); }

    public void deleteById(Integer id) { genreRepository.deleteById(id); }
    public Iterable<Book> getByGenreId(Integer id)
    {
        return bookRepository.findByGenresId(id);
    }

    private Genre getByIdOrThrowException(Integer id) throws IllegalArgumentException {
        Optional<Genre> object = genreRepository.findById(id);

        if (object.isEmpty())
            throw new IllegalArgumentException("Object with such id doesn't exist");

        return object.get();
    }
}
