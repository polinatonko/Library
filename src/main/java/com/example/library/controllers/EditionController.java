package com.example.library.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/editions")
public class EditionController {
    public String getAllEditions(Model model)
    {
        /*List<Edition> books = bookService.getAll();
        model.addAttribute("editions", books);*/
        return "editions";
    }
}
