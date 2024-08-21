package com.example.library.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

import java.util.Date;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Date date;
    @Range(min=1, max=5)
    private int rating;
    @Column(name = "content", length = 4096 * 4)
    private String content;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToOne
    @JoinColumn(name = "edition_id")
    private Edition edition;
    public Review(Date date, int rating, String content, User user, Edition edition)
    {
        this.date = date;
        this.rating = rating;
        this.content = content;
        this.user = user;
        this.edition = edition;
    }
    @PreRemove
    private void removeAssociations()
    {
        user.getReviews().remove(this);
        edition.getReviews().remove(this);
    }
}
