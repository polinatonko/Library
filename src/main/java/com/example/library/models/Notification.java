package com.example.library.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToOne
    @JoinColumn(name = "edition_id")
    private Edition edition;
    public Notification(User user, Edition edition)
    {
        this.user = user;
        this.edition = edition;
    }
    @PreRemove
    private void removeAssociations()
    {
        user.getNotifications().remove(this);
        edition.getNotifications().remove(this);
    }
}
