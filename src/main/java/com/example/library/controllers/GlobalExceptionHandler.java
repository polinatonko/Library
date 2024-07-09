package com.example.library.controllers;

import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.ui.Model;

@Order(-1)
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler({NoHandlerFoundException.class})
    public String handleNotFoundError(NoHandlerFoundException ex, Model model) {
        String errorMessage = "This page is not found!";
        model.addAttribute("error_msg", errorMessage);
        return "error";
    }

    @ExceptionHandler(HttpClientErrorException.Forbidden.class)
    public String accessDeniedException(HttpClientErrorException.Forbidden ex, Model model) {
        String errorMessage = "Access denied!";
        model.addAttribute("error_msg", errorMessage);
        return "error";
    }
}
