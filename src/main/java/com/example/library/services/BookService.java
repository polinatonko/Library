package com.example.library.services;

import com.example.library.GlobalFunctions;
import com.example.library.models.Author;
import com.example.library.models.Book;
import com.example.library.models.Edition;
import com.example.library.models.Genre;
import com.example.library.repositories.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookService {
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private GenreService genreService;
    @Autowired
    private AuthorService authorService;
    @Autowired
    private GlobalFunctions utils;
    public Iterable<Book> search(Specification<Book> spec) { return bookRepository.findAll(spec); }
    public void updateRating(Book book, int value, boolean add)
    {
        int count = book.getReviews().size();
        double rating = book.getRating() * count;
        if (add)
            rating = (rating + value) / (count + 1);
        else
            rating = (count > 1) ? (rating - value) / (count - 1) : 0;

        book.setRating(rating);
        bookRepository.save(book);
    }
    public boolean existsById(Integer id) { return bookRepository.existsById(id); }
    public void unBook(Book book)
    {
        book.unBook();
        bookRepository.save(book);
    }
    public void book(Book book)
    {
        book.book();
        bookRepository.save(book);
    }
    public Iterable<Book> getNew() { return bookRepository.findByReceiptDateGreaterThan(utils.getDate(-7,0,0,0));}
    public Iterable<Book> getAll() { return bookRepository.findAll(); }

    public Book save(Book book) { return bookRepository.save(book); }

    public void deleteById(Integer id) { bookRepository.deleteById(id);}

    public Book getById(Integer id) { return getByIdOrThrowException(id); }

    public void deleteGenre(Integer id, Integer genreId) {
        Pair<Book, Genre> p = checkBookGenre(id, genreId);
        Book book = p.getFirst();
        Genre genre = p.getSecond();
        book.removeGenre(genre);
        save(book);
        genre.removeEdition(book);
        genreService.save(genre);
    }

    public void deleteAuthor(Integer id, Integer authorId) {
        Pair<Book, Author> p = checkBookAuthor(id, authorId);
        Book book = p.getFirst();
        Author author = p.getSecond();
        book.removeAuthor(author);
        save(book);
        author.removeBook(book);
        authorService.save(author);
    }


    public void addGenre(Integer id, Integer genreId) {
        Book book = getByIdOrThrowException(id);
        Genre genre = genreService.getById(genreId);
        genre.addEdition(book);
        genreService.save(genre);
        book.addGenre(genre);
        save(book);
    }

    public void addAuthor(Integer id, Integer authorId) {
        Book book = getByIdOrThrowException(id);
        Author author = authorService.findById(authorId);
        author.addBook(book);
        authorService.save(author);
        book.addAuthor(author);
        save(book);
    }

    private Pair<Book, Genre> checkBookGenre(Integer id, Integer genreId) throws IllegalArgumentException {
        Book book = getByIdOrThrowException(id);
        Genre genre = genreService.getById(genreId);
        if (!book.getGenres().contains(genre))
            throw new IllegalArgumentException("Illegal value of id!");
        return Pair.of(book, genre);
    }

    private Pair<Book, Author> checkBookAuthor(Integer id, Integer authorId) throws IllegalArgumentException {
        Book book = getByIdOrThrowException(id);
        Author author = authorService.findById(authorId);
        if (!book.getAuthors().contains(author))
            throw new IllegalArgumentException("Illegal value of id!");
        return Pair.of(book, author);
    }

    private Book getByIdOrThrowException(Integer id) throws IllegalArgumentException {
        Optional<Book> object = bookRepository.findById(id);

        if (object.isEmpty())
            throw new IllegalArgumentException("Object with such id doesn't exist");

        return object.get();
    }
}
