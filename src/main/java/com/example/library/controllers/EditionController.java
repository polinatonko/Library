package com.example.library.controllers;

import com.example.library.services.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/editions")
public class EditionController {
    @Autowired
    private BookService bookService;

    public String getAllEditions(Model model)
    {
        /*List<Edition> books = bookService.getAll();
        model.addAttribute("editions", books);*/
        return "editions";
    }
}
