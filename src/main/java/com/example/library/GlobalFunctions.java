package com.example.library;

import com.example.library.config.CustomUserDetails;
import com.example.library.models.User;
import com.example.library.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
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

    public String getCurrentUrl(HttpServletRequest request) {
        StringBuilder url = new StringBuilder(request.getRequestURL().toString());
        String queryString = request.getQueryString();
        if (queryString != null) {
            url.append("?").append(queryString);
        }
        return url.toString();
    }

    public Date getDate(int days, int hours, int minutes, int seconds)
    {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR, days * 24 + hours);
        cal.add(Calendar.MINUTE, minutes);
        cal.add(Calendar.SECOND, seconds);
        return new Date(cal.getTime().getTime());
    }

    public Date getCurentDate()
    {
        Calendar cal = Calendar.getInstance();
        return new Date(cal.getTime().getTime());
    }
}
