package com.example.library.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PasswordDto {
    @NotEmpty
    @NotNull
    private String password;
    private String matchingPassword;
    private String token;
}
