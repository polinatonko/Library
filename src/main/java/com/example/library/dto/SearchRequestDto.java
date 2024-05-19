package com.example.library.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SearchRequestDto {
    private String column;
    private String value;
    private Operation operation;

    public enum Operation {
        EQUAL,
        LIKE,
        IN,
        BETWEEN,
        JOIN,
        JOIN_IN,
        FIND,
        LESS_THAN,
        GREATER_THAN
    }
}
