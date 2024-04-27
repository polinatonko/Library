package com.example.library.controllers;

import com.example.library.dto.ObjectsListDto;
import com.example.library.models.Book;
import com.example.library.services.BookService;
import com.example.library.services.BookingService;
import com.example.library.services.GenreService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {
    @Autowired
    private GenreService genreService;
    @Autowired
    private BookService bookService;
    @GetMapping(value = "/")
    public String index(Model model)
    {
        model.addAttribute("genres", genreService.getAll());
        return "pages/index";
    }

    @GetMapping(value = "/new")
    public String newEditions(Model model)
    {
        model.addAttribute("form", new ObjectsListDto<Book>(bookService.getNew()));
        model.addAttribute("title", "New editions");
        return "lists/books";
    }
}
