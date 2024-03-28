package com.example.library.tokens;

import com.example.library.models.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Calendar;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class PasswordResetToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String token;

    @ManyToOne(targetEntity = User.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    @Temporal(TemporalType.TIMESTAMP)
    private Date expiryDate;

    public PasswordResetToken(String token, User user, int timeInHours)
    {
        this.token = token;
        this.user = user;
        this.expiryDate = calculateExpiryDate(timeInHours);
    }

    private Date calculateExpiryDate(int timeInHours)
    {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR, timeInHours);
        return new Date(cal.getTime().getTime());
    }
}
