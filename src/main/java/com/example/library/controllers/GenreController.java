package com.example.library.controllers;
import com.example.library.GlobalFunctions;
import com.example.library.dto.*;
import com.example.library.models.Book;
import com.example.library.models.Genre;
import com.example.library.services.BookService;
import com.example.library.services.GenreService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.annotation.Secured;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


@Controller
@Validated
@RequestMapping("genres")
public class GenreController {
    @Autowired
    private GenreService genreService;
    @Autowired
    private BookService bookService;
    @Autowired
    private GlobalFunctions utils;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String index(
            Model model) {
        model.addAttribute("form", new ObjectsListDto<>(genreService.getAll()));
        return "lists/genres";
    }

    @Secured({"ROLE_ADMIN", "ROLE_LIBRARIAN"})
    @GetMapping(value = "/add-genre")
    public String addGenreDisplayForm(Model model) {
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
            return "redirect:/add-genre";
        }

        genreService.save(newGenre);

        return "redirect:/genres";
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
            @PathVariable("id") Integer id,
            @RequestParam("page") Optional<Integer> page,
            @RequestParam("size") Optional<Integer> size,
            HttpServletRequest httpRequest,
            Model model
    )
    {
        Genre genre = genreService.getById(id);
        model.addAttribute("genre", genre);
        model.addAttribute("genres", genreService.getAll());

        RequestDto request = new RequestDto();
        request.setPageDto(new PageRequestDto(page, size));

        Page<Book> bookPage = bookService.getSearchPaginatedPageForGenre(request, id);
        model.addAttribute("form", new ObjectsListDto<>(bookPage));

        String url = utils.getCurrentUrl(httpRequest);
        if (url.contains("page="))
        {
            url = url.replaceFirst("[&?]page=[\\d]+[?]?", "");
            System.out.println(url);
        }
        char firstDelimeter = url.contains("=") ? '&' : '?';

        model.addAttribute("currentUrl", url);
        model.addAttribute("firstDelimeter", firstDelimeter);

        int totalPages = bookPage.getTotalPages();
        System.out.println(totalPages);
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }

        return "profiles/genre";
    }

    @PostMapping(value = "/edit/{id}")
    public String edit(
            @ModelAttribute("form") GenreListDto form,
            @PathVariable("id") Integer id,
            Model model,
            HttpServletRequest request
    )
    {
        Genre edited = form.getObjects().getLast();
        edited.setId(id);
        genreService.save(edited);

        return utils.getPreviousUrl(request);
    }
}
