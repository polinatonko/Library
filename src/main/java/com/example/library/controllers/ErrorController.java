package com.example.library.controllers;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ErrorController implements org.springframework.boot.web.servlet.error.ErrorController {
    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        int statusCode = (int) status;
        final String error_msg;

        switch (statusCode) {
            case 400:
                error_msg = "Bad request!";
                break;
            case 401:
                error_msg = "Unauthorized!";
                break;
            case 500:
                error_msg = "Internal server error!";
                break;
            case 403:
                error_msg = "Access denied";
                break;
            default:
                error_msg = "Oops! Something went wrong";
        }

        model.addAttribute("error_msg", error_msg);
        return "pages/error";
    }
}
