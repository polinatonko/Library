package com.example.library.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private String url;

    @OneToOne(mappedBy = "photo")
    @JoinColumn(name = "author_id")
    private Author author;

    @OneToOne(mappedBy = "photo")
    @JoinColumn(name = "edition_id")
    private Edition edition;
}
