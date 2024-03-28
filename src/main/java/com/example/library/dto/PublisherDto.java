package com.example.library.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PublisherDto {
    @NotNull
    @NotEmpty
    private String name;
    @NotNull
    @NotEmpty
    private String description;
    @NotNull
    @NotEmpty
    private String address;
    @NotNull
    @NotEmpty
    private String ISBNPrefix;
}
