package com.example.library.models;

import com.example.library.dto.UserDto;
import com.example.library.tokens.VerificationToken;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User {
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
    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;
    @OneToMany(mappedBy = "reader", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Review> reviews;
    //private Set<Action> actions;

    /*@ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private List<Role> roles;*/

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
}
