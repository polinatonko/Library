package com.example.library.controllers;

import com.example.library.config.CustomUserDetails;
import com.example.library.models.User;
import com.example.library.services.BookService;
import com.example.library.services.GenreService;
import com.example.library.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {
    @Autowired
    private GenreService genreService;
    @Autowired
    private BookService bookService;
    @Autowired
    private UserService userService;
    @GetMapping(value = "/")
    public String index(Model model)
    {
        model.addAttribute("genres", genreService.getAll());
        return "pages/index";
    }

    @GetMapping(value = "/new")
    public String newEditions(Model model)
    {
        model.addAttribute("books", bookService.getNew());
        return "lists/recommend";
    }

    @GetMapping(value = "/recommend")
    public String recommend(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model)
    {
        User user = userService.getById(userDetails.getId()).get();
        model.addAttribute("books", bookService.getRecommend(user));
        return "lists/recommend";
    }
}
