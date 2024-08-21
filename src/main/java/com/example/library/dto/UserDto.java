package com.example.library.dto;

import com.example.library.enums.ERole;
import com.example.library.models.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//@PasswordMatches(message = "Passwords don't match")
@Data
@NoArgsConstructor
public class UserDto {
    @NotNull
    @NotEmpty(message = "Name cannot be empty")
    private String name;
    @NotNull
    @NotEmpty(message = "Middle name cannot be empty")
    private String middleName;
    @NotNull
    @NotEmpty(message = "Last name cannot be empty")
    private String lastName;
    @NotNull
    @NotEmpty(message = "Email cannot be empty")
    @Email(message = "Please provide a valid email")
    private String email;
    @NotNull
    @NotEmpty(message = "Password cannot be empty")
    private String password;
    @NotNull
    @NotEmpty(message = "Password cannot be empty")
    private String matchingPassword;
    @NotNull
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private Date birthDate;
    private ERole role;

    public UserDto(User user)
    {
        name = user.getName();
        lastName = user.getLastName();
        middleName = user.getMiddleName();
        email = user.getEmail();
        birthDate = user.getBirthDate();
        password = matchingPassword = user.getPassword();
    }

    public boolean checkEqualPasswords()
    {
        return password.equals(matchingPassword);
    }

    public boolean validatePassword()
    {
        Pattern DIGITS = Pattern.compile("[0-9]"), LETTERS = Pattern.compile("[a-zA-Z]");
        Matcher d = DIGITS.matcher(password), l = LETTERS.matcher(password);

        if (password.length() < 8 || !d.find() || !l.find())
            return false;
        return true;
    }
}
