package com.example.library.services;

import com.example.library.repositories.PasswordResetTokenRepository;
import com.example.library.tokens.PasswordResetToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;

@Service
public class SecurityService {
    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepositoryTokenRepository;

    public boolean isTokenExists(PasswordResetToken token)
    {
        return (token != null);
    }

    public boolean isTokenExpired(PasswordResetToken token)
    {
        Date now = Calendar.getInstance().getTime();
        return token.getExpiryDate().before(now);
    }

    public String checkToken(String token)
    {
        PasswordResetToken passwordResetToken = passwordResetTokenRepositoryTokenRepository.findByToken(token);

        if (!isTokenExists(passwordResetToken))
            return "invalid-token";
        if (isTokenExpired(passwordResetToken))
            return "expired-token";

        return null;
    }
}
