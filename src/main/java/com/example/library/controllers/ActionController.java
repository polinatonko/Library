package com.example.library.controllers;

import com.example.library.GlobalFunctions;
import com.example.library.dto.IssuanceDto;
import com.example.library.dto.PeriodDto;
import com.example.library.enums.BookingStatus;
import com.example.library.models.Edition;
import com.example.library.models.Issuance;
import com.example.library.models.Return;
import com.example.library.models.User;
import com.example.library.services.*;
import com.example.library.timer.BlockTimerTask;
import com.example.library.timer.IssuanceTimerTask;
import jakarta.persistence.Access;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.InvalidParameterException;
import java.util.Date;

@Controller
public class ActionController {
    @Autowired
    private IssuanceService issuanceService;
    @Autowired
    private ReturnService returnService;
    @Autowired
    private BlockService blockService;
    @Autowired
    private BookService bookService;
    @Autowired
    private BookingService bookingService;
    @Autowired
    private UserService userService;
    @Autowired
    private TimerService timerService;
    @Autowired
    private GlobalFunctions utils;

    @PostMapping(value = "/cancel-issuance/{id}")
    public String deleteIssuance(HttpServletRequest request,
                                 @PathVariable("id") Integer id,
                                 Model model)
    {
        Issuance issuance = issuanceService.getById(id);
        issuanceService.delete(issuance);

        return utils.getPreviousUrl(request);
    }

    @PostMapping(value = "/create-issuance")
    public String issuance(HttpServletRequest request,
                           @ModelAttribute("action") @Valid IssuanceDto issuanceDto,
                           Model model)
    {
        Integer editionId = issuanceDto.getEditionId(), userId = issuanceDto.getUserId();
        Edition edition = bookService.getById(editionId);
        User user = userService.getById(userId).get(); // fix!

        // create and save:
        Issuance issuance = new Issuance(edition, user, utils.getDate(30, 0, 0, 0), utils.getCurentDate());
        issuanceService.create(issuance, new IssuanceTimerTask(issuance));


        // cancel booking, if exists:
        BookingStatus bookingStatus = bookingService.checkBooking(editionId, userId);
        if (bookingStatus == BookingStatus.DISABLED)
            bookingService.cancelBooking(editionId, userId);

        return "redirect:/manage-actions?type=issuance";

    }
    @PostMapping(value = "/create-return")
    public String returns(HttpServletRequest request,
                          @ModelAttribute("action") @Valid IssuanceDto issuanceDto,
                          RedirectAttributes redirectAttributes,
                          Model model)
    {
        Integer editionId = issuanceDto.getEditionId(), userId = issuanceDto.getUserId();
        Edition edition = bookService.getById(editionId);
        User user = userService.getById(userId).get(); // fix!

        // check if issuance exists
        if (issuanceService.exists(editionId, userId))
        {
            // create and save:
            Return ret = new Return(edition, user, utils.getDate(0, 0, 0, 0));
            returnService.create(ret);
        }
        else
        {
            redirectAttributes.addFlashAttribute("returnError", true);
            return utils.getPreviousUrl(request);
        }

        return "redirect:/manage-actions?type=return";
    }
    @GetMapping(value = "/issuance")
    public String issuanceForm(HttpServletRequest request,
                               Model model)
    {
        model.addAttribute("books", bookService.getAll());
        model.addAttribute("users", userService.getEnabledUsers());
        model.addAttribute("action", new IssuanceDto());
        return "addForms/issuance";
    }
    @GetMapping(value = "/return")
    public String returnForm(HttpServletRequest request,
                             Model model)
    {
        model.addAttribute("books", bookService.getAll());
        model.addAttribute("users", userService.getEnabledUsers());
        model.addAttribute("action", new IssuanceDto());
        return "addForms/return";
    }
    @Secured({"ROLE_ADMIN", "ROLE_LIBRARIAN"})
    @GetMapping(value = "/manage-actions")
    public String manageActions(
            HttpServletRequest request,
            @RequestParam("type") String type,
            @ModelAttribute("period") PeriodDto period,
            Model model)
    {
        String template = "lists/";
        boolean filter = period.getFrom() != null;
        Date from = period.getFrom(), to = period.getTo();
        switch (type) {
            case ("block"):
                model.addAttribute("actions", filter ? blockService.getByPeriod(from, to) : blockService.getAll());
                template += "blocks";
                break;
            case ("booking"):
                model.addAttribute("actions", filter ? bookingService.getByPeriod(from, to) : bookingService.getAll());
                template += "bookings";
                break;
            case ("issuance"):
                model.addAttribute("actions", filter ? issuanceService.getAllByPeriod(from, to) : issuanceService.getAll());
                template += "issuances";
                break;
            case ("return"):
                model.addAttribute("actions", filter ? returnService.getByPeriod(from, to) : returnService.getAll());
                template += "returns";
                break;
            default:
                throw new InvalidParameterException("There is no resource on such uri!");
        }
        model.addAttribute("period", period);
        return template;
    }
}
