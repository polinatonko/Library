package com.example.library.services;

import com.example.library.models.Author;
import com.example.library.repositories.AuthorRepository;
import com.example.library.repositories.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthorService {
    @Autowired
    private AuthorRepository authorRepository;

    public Iterable<Author> getAll() { return authorRepository.findAll(); }

    public Author save(Author object) { return authorRepository.save(object); }
    public void delete(Integer id) { authorRepository.deleteById(id); }

    public void edit(Author object)
    {
        Author saved = getByIdOrThrowException(object.getId());
        saved.setFullName(object.getFullName());
        saved.setBio(object.getBio());
        authorRepository.save(saved);
    }

    public Author findById(Integer id) { return getByIdOrThrowException(id); }

    private Author getByIdOrThrowException(Integer id) throws IllegalArgumentException {
        Optional<Author> object = authorRepository.findById(id);

        if (object.isEmpty())
            throw new IllegalArgumentException("Object with such id doesn't exist");

        return object.get();
    }

}
