package com.example.library.services;

import com.example.library.dto.PageRequestDto;
import com.example.library.dto.RequestDto;
import com.example.library.dto.SearchRequestDto;
import com.example.library.models.Author;
import com.example.library.models.Book;
import com.example.library.repositories.AuthorRepository;
import com.example.library.repositories.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class AuthorService {
    @Autowired
    private AuthorRepository authorRepository;
    public Page<Author> getSearchPaginatedPage(RequestDto request) {
        Pageable pageable = new PageRequestDto().getPageable(request.getPageDto());

        return authorRepository.findAll(pageable);
    }

    public String getNamesByIds(String selectedAuthors)
    {
        List<String> ids = Arrays.stream(selectedAuthors.split(",")).toList(), authors = new ArrayList<>();
        for (String id : ids)
            authors.add(getNameById(Integer.parseInt(id)));
        return String.join(",", authors);
    }

    private String getNameById(Integer id) {
        Optional<Author> author = authorRepository.findById(id);
        return author.isPresent() ? author.get().getFullName() : "";
    }

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
