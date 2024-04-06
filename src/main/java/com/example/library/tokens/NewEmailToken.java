package com.example.library.tokens;

import com.example.library.models.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Calendar;
import java.util.Date;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class NewEmailToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String token;

    private String newEmail;

    @OneToOne(mappedBy = "newEmailToken")
    @JoinColumn(name = "user_id")
    private User user;

    @Temporal(TemporalType.TIMESTAMP)
    private Date expiryDate;

    public NewEmailToken(String token, String newEmail, User user, int timeInHours)
    {
        this.token = token;
        this.newEmail = newEmail;
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
