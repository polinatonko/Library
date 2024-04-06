package com.example.library.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import java.io.IOException;

public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException ex)
            throws IOException, ServletException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());

        String error;
        if (ex instanceof BadCredentialsException) {
            error = "bad-credentials";
        } else if (ex instanceof DisabledException) {
            error = "disabled";
        } else if (ex instanceof LockedException) {
            error = "locked";
        } else {
            error = "true";
        }

        response.sendRedirect("/login?error=" + error);
    }
}
