package com.example.library.controllers;

import com.example.library.GlobalFunctions;
import com.example.library.dto.ObjectsListDto;
import com.example.library.dto.AuthorListDto;
import com.example.library.dto.PageRequestDto;
import com.example.library.dto.RequestDto;
import com.example.library.models.Author;
import com.example.library.services.AuthorService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


@Controller
@RequestMapping("/authors")
public class AuthorController {
    @Autowired
    private AuthorService authorService;
    @Autowired
    private GlobalFunctions utils;

    @GetMapping
    public String index(@RequestParam("page") Optional<Integer> page,
                        @RequestParam("size") Optional<Integer> size,
                        Model model,
                        HttpServletRequest httpRequest)
    {
        RequestDto request = new RequestDto();

        if (size.isEmpty())
            size = Optional.of(10);
        request.setPageDto(new PageRequestDto(page, size));

        Page<Author> authorPage = authorService.getSearchPaginatedPage(request);
        model.addAttribute("form", new ObjectsListDto<Author>(authorPage));

        String url = utils.getCurrentUrl(httpRequest);
        if (url.contains("page="))
        {
            url = url.replaceFirst("[&?]page=[\\d]+[?]?", "");
            System.out.println(url);
        }
        char firstDelimeter = url.contains("=") ? '&' : '?';

        model.addAttribute("currentUrl", url);
        model.addAttribute("firstDelimeter", firstDelimeter);

        int totalPages = authorPage.getTotalPages();
        System.out.println(totalPages);
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }

        return "lists/authors-cards";
    }

    @Secured({"ROLE_ADMIN","ROLE_LIBRARIAN"})
    @GetMapping(value = "/add")
    public String addForm(Model model)
    {
        model.addAttribute("object", new Author());
        return "addForms/author";
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
        authorService.delete(id);

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
        return "profiles/author_profile";
    }
}
