package com.example.library.enums;

import lombok.Getter;

@Getter
public enum EditionFormat {
    SOFT_COVER ("soft cover"),
    HARD_COVER ("hard cover");

    private final String name;

    EditionFormat(String name) {
        this.name = name;
    }
}
