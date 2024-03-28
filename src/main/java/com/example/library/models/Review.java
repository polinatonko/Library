package com.example.library.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User reader;
    private Date date;
    private int rating;
    @Column(name = "content", length = 4096 * 4)
    private String content;
    @ManyToOne
    @JoinColumn(name = "edition_id")
    private Edition edition;
}
