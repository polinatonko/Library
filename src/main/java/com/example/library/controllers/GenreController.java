package com.example.library.controllers;
import com.example.library.GlobalFunctions;
import com.example.library.models.Genre;
import com.example.library.services.BookService;
import com.example.library.services.GenreService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;


@Controller
@Validated
@RequestMapping("genres")
public class GenreController {
    @Autowired
    private GenreService genreService;
    @Autowired
    private GlobalFunctions utils;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String index(Model model) {
        model.addAttribute("title", "Genres");
        model.addAttribute("genres", genreService.getAll());
        return "lists/genreList";
    }

    @Secured({"ROLE_ADMIN", "ROLE_LIBRARIAN"})
    @GetMapping(value = "/add-genre")
    public String addGenreDisplayForm(Model model) {
        model.addAttribute("title", "Add genre");
        model.addAttribute(new Genre());
        return "addForms/addGenreForm";
    }

    @Secured({"ROLE_ADMIN", "ROLE_LIBRARIAN"})
    @PostMapping(value = "/add-genre")
    public String addGenreProcessForm(
            @ModelAttribute @Valid @RequestBody Genre newGenre,
            Errors errors,
            Model model) {
        if (errors.hasErrors()) {
            model.addAttribute("title", "Add genre");
            return "redirect:/add-genre";
        }

        genreService.save(newGenre);

        model.addAttribute("title", "Add genre");
        return "redirect:";
    }

    @Secured({"ROLE_ADMIN", "ROLE_LIBRARIAN"})
    @PostMapping(value = "delete-genre/{id}")
    public String deleteGenre(
            @PathVariable("id") Integer id,
            HttpServletRequest request,
            Model model) {
        genreService.deleteById(id);

        return utils.getPreviousUrl(request);
    }

    @GetMapping(value = "/genre/{id}")
    public String genreEditions(
            HttpServletRequest request,
            @PathVariable("id") Integer id,
            Model model
    )
    {
        Genre genre = genreService.getById(id);
        model.addAttribute("title", genre.getName());
        model.addAttribute("genre", genre);
        model.addAttribute("books", genreService.getByGenreId(id));
        model.addAttribute("genres", genreService.getAll());
        return "profiles/genre";
    }
}
