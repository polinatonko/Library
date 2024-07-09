package com.example.library.services;

import com.example.library.GlobalFunctions;
import com.example.library.dto.PageRequestDto;
import com.example.library.dto.RequestDto;
import com.example.library.dto.SearchRequestDto;
import com.example.library.models.*;
import com.example.library.repositories.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class BookService {
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private GenreService genreService;
    @Autowired
    private AuthorService authorService;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private FilterSpecification<Book> filterSpecification;
    @Autowired
    private GlobalFunctions utils;
    public Page<Book> getSearchPaginatedPageForGenre(RequestDto request, Integer genreId)
    {
        Pageable pageable = new PageRequestDto().getPageable(request.getPageDto());

        return bookRepository.findByGenresId(genreId, pageable);
    }
    public Page<Book> getSearchPaginatedPageForPublisher(RequestDto request, Integer publisherId)
    {
        Pageable pageable = new PageRequestDto().getPageable(request.getPageDto());

        return bookRepository.findByPublisherId(publisherId, pageable);
    }
    public Page<Book> getSearchPaginatedPage(RequestDto request, String selectedAuthors, String selectedPublishers, String selectedFormats,
                                             String selectedGenres, String minAge, String maxAge, String keywords) {
        Pageable pageable = new PageRequestDto().getPageable(request.getPageDto());

        if (selectedAuthors != null)
            request.add("authors-fullName", selectedAuthors, SearchRequestDto.Operation.JOIN_IN);

        if (selectedPublishers != null)
            request.add("publisher-name", selectedPublishers, SearchRequestDto.Operation.JOIN_IN);

        if (selectedFormats != null)
            request.add("format", selectedFormats, SearchRequestDto.Operation.IN);

        if (selectedGenres != null)
            request.add("genres-name", selectedGenres, SearchRequestDto.Operation.JOIN_IN);

        request.add("ageLimit", String.join(",", minAge, maxAge), SearchRequestDto.Operation.BETWEEN);

        if (keywords != null)
            request.add(String.join("-", "name", "about"), keywords, SearchRequestDto.Operation.FIND);

        Specification<Book> spec = filterSpecification.getSearchSpecification(request.getSearchRequestDtos());

        Page<Book> page;
        if (request.getSearchRequestDtos().isEmpty())
        {
            List<Book> books = (List<Book>)getAll();
            page = new PageImpl<Book>(books, PageRequest.of(pageable.getPageNumber(), pageable.getPageSize()), books.size());
        }
        else
            page = search(spec, pageable);

        return page;
    }
    public List<Book> getByIds(List<Integer> ids)
    {
        return bookRepository.findAllById(ids);
    }
    public Iterable<Book> getAllFree()
    {
        RequestDto request = new RequestDto();
        request.add("copiesCount", "0", SearchRequestDto.Operation.GREATER_THAN);
        return search(filterSpecification.getSearchSpecification(request.getSearchRequestDtos()));
    }
    public List<Book> getRecommend(User user)
    {
        List<Book> books = bookRepository.findAll();
        books.sort(new Comparator<Book>() {
            @Override
            public int compare(Book a, Book b) {
                int aCoeff = a.getCoefficient(user.getLikes()), bCoeff = b.getCoefficient(user.getLikes());
                return aCoeff > bCoeff ? +1 : aCoeff < bCoeff ? -1 : 0;
            }
        });

        return books.stream().limit(20).collect(Collectors.toList());
    }
    public int getMinAge()
    {
        return Collections.min(bookRepository.findAll(), Comparator.comparing(Edition::getAgeLimit)).getAgeLimit();
    }

    public int getMaxAge()
    {
        return Collections.max(bookRepository.findAll(), Comparator.comparing(Edition::getAgeLimit)).getAgeLimit();
    }
    public Iterable<Book> search(Specification<Book> spec) { return bookRepository.findAll(spec); }
    public Page<Book> search(Specification<Book> spec, Pageable pageable) { return bookRepository.findAll(spec, pageable); }
    public void updateRating(Book book, int value, boolean add)
    {
        int count = book.getReviews().size();
        double rating = book.getRating() * count;
        if (add)
            rating = (rating + value) / (count + 1);
        else
            rating = (count > 1) ? (rating - value) / (count - 1) : 0;

        book.setRating(rating);
        save(book);
    }
    public boolean existsById(Integer id) { return bookRepository.existsById(id); }
    public void unBook(Book book)
    {
        book.unBook();
        save(book);
    }
    public void book(Book book)
    {
        book.book();
        save(book);
    }
    public Iterable<Book> getNew()
    {
        return bookRepository.findByReceiptDateGreaterThan(utils.getDate(-7,0,0,0));
    }
    public Iterable<Book> getAll() { return bookRepository.findAll(); }

    public Book save(Book book)
    {
        Integer id = book.getId();

        if (id != null)
        {
            Book saved = getByIdOrThrowException(id);

            if (saved.getCopiesCount() == 0 && book.getCopiesCount() > 0)
                notificationService.notify(book);
        }

        return bookRepository.save(book);
    }

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
