package com.example.library.controllers;

import com.example.library.dto.ObjectsListDto;
import com.example.library.models.Book;
import com.example.library.services.BookService;
import com.example.library.services.GenreService;
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
        model.addAttribute("title", "Genres");
        model.addAttribute("genres", genreService.getAll());
        return "pages/index";
    }

    @GetMapping(value = "/new")
    public String newEditions(Model model)
    {
        model.addAttribute("title", "New editions");
        model.addAttribute("form", new ObjectsListDto<Book>(bookService.getNew()));
        return "lists/books";
    }
}
