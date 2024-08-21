package com.example.library.controllers;

import com.example.library.GlobalFunctions;
import com.example.library.dto.ImageDto;
import com.example.library.models.Author;
import com.example.library.models.Book;
import com.example.library.models.Image;
import com.example.library.services.AuthorService;
import com.example.library.services.BookService;
import com.example.library.services.ImageService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@Controller
@RequestMapping("/images")
public class ImageController {
    @Autowired
    private ImageService imageService;
    @Autowired
    private AuthorService authorService;
    @Autowired
    private BookService bookService;
    @Autowired
    private GlobalFunctions utils;
    @Secured({"ROLE_ADMIN", "ROLE_LIBRARIAN"})
    @GetMapping(value = "/delete-author/{id}")
    public String deleteImage1(
            HttpServletRequest request,
            @PathVariable("id") Integer id
            ) throws IOException {
        Author author = authorService.findById(id);
        imageService.deleteImage(author);
        return utils.getPreviousUrl(request);
    }

    @Secured({"ROLE_ADMIN", "ROLE_LIBRARIAN"})
    @GetMapping(value = "/delete-book/{id}")
    public String deleteImage2(
            HttpServletRequest request,
            @PathVariable("id") Integer id
    ) throws IOException {
        Book book = bookService.getById(id);
        imageService.deleteImage(book);
        return utils.getPreviousUrl(request);
    }

    @Secured({"ROLE_ADMIN", "ROLE_LIBRARIAN"})
    @PostMapping(value = "/upload/{authorId}")
    public String uploadImage(
            HttpServletRequest request,
            ImageDto imageDto,
            @PathVariable("authorId") Integer authorId,
            RedirectAttributes redirectAttributes)
    {
        Author author = authorService.findById(authorId);
        Image img = imageService.uploadImage(imageDto, author);
        author.setPhoto(img);
        authorService.save(author);
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
