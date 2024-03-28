package com.example.library.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ERole {
    @JsonProperty("reader")
    ROLE_READER("reader"),
    @JsonProperty("librarian")
    ROLE_LIBRARIAN("librarian"),
    @JsonProperty("admin")
    ROLE_ADMIN("admin");

    private final String name;

    ERole(String name)
    {
        this.name = name;
    }
}
