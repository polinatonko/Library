package com.example.library.controllers;

import com.example.library.GlobalFunctions;
import com.example.library.dto.ObjectsListDto;
import com.example.library.dto.PublisherDto;
import com.example.library.dto.PublishersListDto;
import com.example.library.models.Publisher;
import com.example.library.services.PublisherService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("publishers")
public class PublisherController {
    @Autowired
    private PublisherService publisherService;
    @Autowired
    private GlobalFunctions utils;

    @GetMapping(value = "")
    public String getPublishers(Model model)
    {
        model.addAttribute("form", new ObjectsListDto<Publisher>(publisherService.getAll()));
        return "publishers";
    }

    @Secured({"ROLE_ADMIN", "ROLE_LIBRARIAN"})
    @GetMapping(value = "/add-publisher")
    public String addPublisher(HttpServletRequest request, Model model)
    {
        model.addAttribute("publisher", new PublisherDto());
        return "addPublisher";
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
        return "redirect:";
    }
}
