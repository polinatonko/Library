package com.example.library.controllers;

import com.example.library.GlobalFunctions;
import com.example.library.dto.ObjectsListDto;
import com.example.library.dto.AuthorListDto;
import com.example.library.models.Author;
import com.example.library.repositories.AuthorRepository;
import com.example.library.services.AuthorService;
import com.example.library.services.ImageService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequestMapping("/authors")
public class AuthorController {
    @Autowired
    private AuthorService authorService;
    @Autowired
    private AuthorRepository authorRepository;
    @Autowired
    private ImageService imageService;

    @Autowired
    private GlobalFunctions utils;

    @GetMapping
    public String index(Model model)
    {
        model.addAttribute("form", new ObjectsListDto<Author>(authorService.getAll()));
        model.addAttribute("title", "Authors");
        return "lists/authors";
    }

    @Secured({"ROLE_ADMIN","ROLE_LIBRARIAN"})
    @GetMapping(value = "/add")
    public String addForm(Model model)
    {
        model.addAttribute("title", "Add author");
        model.addAttribute("object", new Author());
        return "addForms/addAuthor";
    }

    @Secured({"ROLE_ADMIN","ROLE_LIBRARIAN"})
    @PostMapping(value = "/add-post")
    public String add(
            HttpServletRequest request,
            @ModelAttribute("object") @Valid Author author,
            RedirectAttributes redirectAttributes,
            Model model,
            Errors errors)
    {
        if (errors.hasErrors())
        {
            return "redirect:/add";
        }

        authorService.save(author);

        return "redirect:/authors";
    }

    @Secured({"ROLE_ADMIN", "ROLE_LIBRARIAN"})
    @PostMapping(value = "delete-author/{id}")
    public String delete(
            @PathVariable("id") Integer id,
            HttpServletRequest request,
            Model model) {
        authorRepository.deleteById(id);

        return utils.getPreviousUrl(request);
    }

    @Secured({"ROLE_ADMIN", "ROLE_LIBRARIAN"})
    @PostMapping(value = "edit-author/{id}")
    public String edit(
            HttpServletRequest request,
            @PathVariable("id") Integer id,
            @ModelAttribute("form") AuthorListDto form,
            Model model
    ){
        Author edited = form.getObjects().getLast();
        edited.setId(id);
        authorService.edit(edited);
        return utils.getPreviousUrl(request);
    }

    @GetMapping(value = "profile/{id}")
    public String profile(

            @PathVariable("id") Integer id,
            Model model
    )
    {
        model.addAttribute("object", authorService.findById(id));
        model.addAttribute("title", "Author");
        return "profiles/author_profile";
    }
}
