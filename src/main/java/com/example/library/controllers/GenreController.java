package com.example.library.controllers;
import com.example.library.GlobalFunctions;
import com.example.library.models.Genre;
import com.example.library.repositories.GenreRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;

import java.util.Optional;

@Controller
@Validated
@RequestMapping("genres")
public class GenreController {
    @Autowired
    private GenreRepository genreRepository;
    @Autowired
    private GlobalFunctions utils;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String index(Model model) {
        model.addAttribute("title", "Genres");
        model.addAttribute("genres", genreRepository.findAll());
        return "genreList";
    }

    @Secured("ROLE_ADMIN")
    @GetMapping(value = "/add-genre")
    public String addGenreDisplayForm(Model model) {
        model.addAttribute("title", "Add genre");
        model.addAttribute(new Genre());
        return "addGenreForm";
    }

    @Secured("ROLE_ADMIN")
    @PostMapping(value = "/add-genre")
    public String addGenreProcessForm(
            @ModelAttribute @Valid @RequestBody Genre newGenre,
            Errors errors,
            Model model) {
        if (errors.hasErrors()) {
            model.addAttribute("title", "Add genre");
            return "redirect:/add-genre";
        }

        genreRepository.save(newGenre);

        model.addAttribute("title", "Add genre");
        return "redirect:";
    }

    @PostMapping(value = "delete-genre")
    public String deleteGenre(
            @RequestParam Integer id,
            HttpServletRequest request,
            Model model) {
        genreRepository.deleteById(id);

        return utils.getPreviousUrl(request);
    }
}
