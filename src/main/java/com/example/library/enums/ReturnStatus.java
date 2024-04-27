package com.example.library.enums;

import lombok.Getter;

@Getter
public enum ReturnStatus {
    ON_TIME ("On time"),
    OVERDUE ("Overdue");

    private final String name;

    ReturnStatus(String name) { this.name = name; }
}
