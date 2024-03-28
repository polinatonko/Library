package com.example.library.enums;

import lombok.Getter;

@Getter
public enum EditionFormat {
    SOFT_COVER ("мягкий переплет"),
    HARD_COVER ("твердый переплет");

    private final String name;

    EditionFormat(String name) {
        this.name = name;
    }
}
