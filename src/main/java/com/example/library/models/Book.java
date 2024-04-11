package com.example.library.models;

import jakarta.persistence.ManyToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Book extends Edition {
    @ManyToMany(mappedBy = "books")
    private Author author;


}
