package com.example.library.models;

import com.example.library.dto.UserDto;
import com.example.library.interfaces.IEntity;
import com.example.library.tokens.NewEmailToken;
import com.example.library.tokens.VerificationToken;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User implements IEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String middleName;
    private String lastName;
    @Email
    @Column(unique = true)
    private String email;
    private String password;
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private Date birthDate;
    private boolean isEnabled;
    private boolean isBlocked;
    private Date blockingEnd;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "verification_token_id")
    private VerificationToken verificationToken;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "new_email_token_id")
    private NewEmailToken newEmailToken;
    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Review> reviews;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Action> actions;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Like> likes;

    public User() {}

    public User(UserDto userDto, String pswd)
    {
        name = userDto.getName();
        middleName = userDto.getMiddleName();
        lastName = userDto.getLastName();
        email = userDto.getEmail();
        birthDate = userDto.getBirthDate();
        password = pswd;
    }

    @Override
    public String getShortName() {
        return String.join(" ", lastName, name, middleName);
    }
}
