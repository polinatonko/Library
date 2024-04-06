package com.example.library;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

@Component
public class GlobalFunctions {
    public String getPreviousUrl(HttpServletRequest request) {
        Optional<String> previousUrl = Optional.ofNullable(request.getHeader("Referer")).map(requestUrl -> "redirect:" + requestUrl);
        return previousUrl.orElse("redirect:");
    }

    public Date getDate(int days, int hours, int minutes, int seconds)
    {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR, days * 24 + hours);
        cal.add(Calendar.MINUTE, minutes);
        cal.add(Calendar.SECOND, seconds);
        return new Date(cal.getTime().getTime());
    }
}
