package com.example.library.models;

import com.example.library.enums.EditionFormat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
public abstract class Edition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String ISBN;
    private String language;
    private Date publishingDate;
    @ManyToOne
    @JoinColumn(name = "publisher_id")
    private Publisher publisher;
    private String name;
    private  int pagesCount;
    private int copiesCount;
    private int ageLimit;
    private double rating;
    private EditionFormat format;
    private String bibliographicRecord;
    @ManyToMany(mappedBy = "editions")
    private List<Genre> genres;
    @OneToMany(mappedBy = "edition", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Review> reviews;
}
