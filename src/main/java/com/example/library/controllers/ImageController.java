package com.example.library.controllers;

import com.example.library.GlobalFunctions;
import com.example.library.dto.ImageDto;
import com.example.library.models.Author;
import com.example.library.models.Book;
import com.example.library.models.Image;
import com.example.library.repositories.AuthorRepository;
import com.example.library.services.BookService;
import com.example.library.services.ImageService;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/images")
public class ImageController {
    @Autowired
    private ImageService imageService;
    @Autowired
    private AuthorRepository authorRepository;
    @Autowired
    private BookService bookService;
    @Autowired
    private GlobalFunctions utils;

    @Secured({"ROLE_ADMIN", "ROLE_LIBRARIAN"})
    @PostMapping(value = "/upload/{authorId}")
    public String uploadImage(
            HttpServletRequest request,
            ImageDto imageDto,
            @PathVariable("authorId") Integer authorId,
            RedirectAttributes redirectAttributes)
    {
        Author author = authorRepository.findById(authorId).get();
        Image img = imageService.uploadImage(imageDto, author);
        author.setPhoto(img);
        authorRepository.save(author);
        return utils.getPreviousUrl(request);
    }

    @Secured({"ROLE_ADMIN", "ROLE_LIBRARIAN"})
    @PostMapping(value = "/upload/book/{bookId}")
    public String upload(
            HttpServletRequest request,
            ImageDto imageDto,
            @PathVariable("bookId") Integer bookId,
            RedirectAttributes redirectAttributes
    )
    {
        Book book = bookService.getById(bookId);
        Image img = imageService.uploadImage(imageDto, book);
        book.setPhoto(img);
        bookService.save(book);
        return utils.getPreviousUrl(request);
    }
}
