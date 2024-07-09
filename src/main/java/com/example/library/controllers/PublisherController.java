package com.example.library.controllers;

import com.example.library.GlobalFunctions;
import com.example.library.dto.ObjectsListDto;
import com.example.library.dto.PageRequestDto;
import com.example.library.dto.PublisherDto;
import com.example.library.dto.PublishersListDto;
import com.example.library.dto.RequestDto;
import com.example.library.models.Book;
import com.example.library.models.Publisher;
import com.example.library.services.BookService;
import com.example.library.services.PublisherService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("publishers")
public class PublisherController {
    @Autowired
    private PublisherService publisherService;
    @Autowired
    private BookService bookService;
    @Autowired
    private GlobalFunctions utils;

    @GetMapping(value = "")
    public String getPublishers(Model model)
    {
        model.addAttribute("form", new ObjectsListDto<Publisher>(publisherService.getAll()));
        return "lists/publishers";
    }

    @Secured({"ROLE_ADMIN", "ROLE_LIBRARIAN"})
    @GetMapping(value = "/add-publisher")
    public String addPublisher(HttpServletRequest request, Model model)
    {
        model.addAttribute("publisher", new PublisherDto());
        return "addForms/addPublisher";
    }

    @Secured({"ROLE_ADMIN", "ROLE_LIBRARIAN"})
    @PostMapping(value = "/delete-publisher/{id}")
    public String deletePublisher(HttpServletRequest request,
                             @PathVariable("id") Integer id,
                             Model model)
    {
        publisherService.deletePublisher(id);
        return utils.getPreviousUrl(request);
    }
    @Secured({"ROLE_ADMIN", "ROLE_LIBRARIAN"})
    @PostMapping(value = "/edit-publisher/{id}")
    public String editPublisher(HttpServletRequest request,
                           @PathVariable("id") Integer id,
                           @ModelAttribute("form") PublishersListDto /*ObjectsListDto<Publisher>*/ form,
                           Model model)
    {
        Publisher edited = form.getObjects().getLast();
        edited.setId(id);
        publisherService.updatePublisher(edited);
        return utils.getPreviousUrl(request);
    }
    @Secured({"ROLE_ADMIN", "ROLE_LIBRARIAN"})
    @PostMapping(value = "/add-publisher-post")
    public String addPublisher(@ModelAttribute("publisher") @Valid PublisherDto publisherDto)
    {
        publisherService.createPublisher(publisherDto);
        return "redirect:/publishers";
    }

    @GetMapping(value = "/{id}")
    public String publisherEditions(
            HttpServletRequest httpRequest,
            @PathVariable("id") Integer id,
            @RequestParam("page") Optional<Integer> page,
            @RequestParam("size") Optional<Integer> size,
            Model model
    )
    {
        Publisher publisher = publisherService.getById(id);
        model.addAttribute("publisher", publisher);
        model.addAttribute("publishers", publisherService.getAll());

        RequestDto request = new RequestDto();
        if (size.isEmpty())
            size = Optional.of(8);
        request.setPageDto(new PageRequestDto(page, size));

        Page<Book> bookPage = bookService.getSearchPaginatedPageForPublisher(request, id);
        model.addAttribute("form", new ObjectsListDto<Book>(bookPage));

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
        return "profiles/publisher";
    }
}
