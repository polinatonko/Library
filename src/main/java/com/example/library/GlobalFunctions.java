package com.example.library;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class GlobalFunctions {
    public String getPreviousUrl(HttpServletRequest request)
    {
        Optional<String> previousUrl = Optional.ofNullable(request.getHeader("Referer")).map(requestUrl -> "redirect:" + requestUrl);
        return previousUrl.orElse("redirect:");
    }
}
