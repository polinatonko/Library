package com.example.library.models;

import com.example.library.enums.EditionFormat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@Inheritance
@DiscriminatorColumn(name = "TYPE")
public abstract class Edition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(unique = true)
    private String ISBN;
    private String language;
    private String publishingDate;
    private Date receiptDate;
    @ManyToOne
    @JoinColumn(name = "publisher_id")
    private Publisher publisher;
    private String name;
    @Column(length = 4096)
    private String about;
    private  int pagesCount;
    private int copiesCount;
    private int ageLimit;
    private double rating;
    private EditionFormat format;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "photo_id")
    private Image photo;
    @ManyToMany(mappedBy = "editions")
    private Set<Genre> genres;
    @OneToMany(mappedBy = "edition", cascade = CascadeType.ALL)
    private Set<Like> likes;
    @OneToMany(mappedBy = "edition", cascade = CascadeType.ALL)
    private Set<Action> actions;
    @OneToMany(mappedBy = "edition", cascade = CascadeType.ALL)
    private Set<Review> reviews;
    @OneToMany(mappedBy = "edition", cascade = CascadeType.ALL)
    private Set<Notification> notifications;

    public void incCopiesCount()
    { copiesCount++; }
    public void decCopiesCount() { copiesCount--; }
    public void book() { copiesCount--; }
    public void unBook() { copiesCount++; }
    public String getShortName() {return name; }
    public void removeGenre(Genre genre) { genres.remove(genre);}
    public void addGenre(Genre genre)
    {
        genres.add(genre);
    }

    @PreRemove
    private void removeAssociations() {
        // many to many
        for (Genre genre: this.genres) {
            genre.getEditions().remove(this);
        }
    }
}
