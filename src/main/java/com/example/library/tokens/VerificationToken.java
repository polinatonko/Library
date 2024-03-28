package com.example.library.tokens;

import com.example.library.models.User;
import jakarta.persistence.*;

import java.util.Calendar;
import java.util.Date;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class VerificationToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String token;

    @OneToOne(mappedBy = "verificationToken")
    @JoinColumn(name = "user_id")
    private User user;

    @Temporal(TemporalType.TIMESTAMP)
    private Date expiryDate;

    public VerificationToken(String token, User user, int timeInHours)
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
