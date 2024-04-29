package com.example.library.controllers;

import com.example.library.GlobalFunctions;
import com.example.library.config.CustomUserDetails;
import com.example.library.dto.ReviewDto;
import com.example.library.models.Review;
import com.example.library.services.IssuanceService;
import com.example.library.services.ReviewService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping(value = "/reviews")
public class ReviewController {
    @Autowired
    private ReviewService reviewService;
    @Autowired
    private IssuanceService issuanceService;
    @Autowired
    private GlobalFunctions utils;
    @Secured({"ROLE_ADMIN", "ROLE_LIBRARIAN"})
    @PostMapping(value = "/delete-review/{id}")
    public String deleteById(HttpServletRequest request,
                             @PathVariable("id") Integer id,
                             Model model)
    {
        Review review = reviewService.getById(id);
        reviewService.delete(review);
        return utils.getPreviousUrl(request);
    }
    @PostMapping(value = "/delete/{id}")
    public String delete(HttpServletRequest request,
                         @AuthenticationPrincipal CustomUserDetails userDetails,
                         @PathVariable("id") Integer editionId,
                         Model model)
    {
        if (userDetails == null)
        {
            // authentication needed
        }
        Integer userId = userDetails.getId();
        reviewService.deleteByIds(userId, editionId);
        return utils.getPreviousUrl(request);
    }
    @GetMapping(value = "/review/{id}")
    public String reviewForm(HttpServletRequest request,
                             @AuthenticationPrincipal CustomUserDetails userDetails,
                             @PathVariable("id") Integer editionId,
                             RedirectAttributes redirectAttributes,
                             Model model
                             )
    {
        Integer userId = userDetails.getId();

        // check if issuance exists
        if (!issuanceService.exists(editionId, userId))
            return reviewExists(redirectAttributes, editionId); // change message

        // check if review already exists
        if (reviewService.existsByIds(userId, editionId))
            return reviewExists(redirectAttributes, editionId);

        model.addAttribute("title", "Leave review");
        model.addAttribute("review", new ReviewDto(editionId));
        return "addForms/review";
    }

    @PostMapping(value = "/create-review")
    public String review(HttpServletRequest request,
                         @AuthenticationPrincipal CustomUserDetails userDetails,
                         @ModelAttribute("review") @Valid ReviewDto reviewDto,
                         RedirectAttributes redirectAttributes,
                         Model model)
    {
        if (userDetails == null)
        {
            // authentication is needed
        }

        Integer userId = userDetails.getId(), editionId = reviewDto.getEditionId();

        // check if review already exists
        if (reviewService.existsByIds(userId, editionId))
            return reviewExists(redirectAttributes, editionId);

        // create
        reviewService.create(reviewDto, userId);
        return "redirect:/books/" + editionId.toString();
    }

    private String reviewExists(RedirectAttributes redirectAttributes, Integer id)
    {
        redirectAttributes.addFlashAttribute("reviewExists", true);
        redirectAttributes.addFlashAttribute("showReviewExists", true);
        return "redirect:/books/{id}";
    }
}
