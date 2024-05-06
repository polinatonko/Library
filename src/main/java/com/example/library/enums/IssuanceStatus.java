package com.example.library.enums;

import lombok.Getter;

@Getter
public enum IssuanceStatus {
    ACTIVE ("Active"),
    RETURNED ("Returned"),
    EXPIRED ("Expired");

    private String name;
    IssuanceStatus(String name) {
        this.name = name;
    }
}
