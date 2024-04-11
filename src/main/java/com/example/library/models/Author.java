package com.example.library.models;

import com.example.library.interfaces.IEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class Author implements IEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String fullName;

    @Column(length = 4096)
    private String bio;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "photo_id")
    private Image photo;

    @ManyToMany

    @Override
    public String getShortName() {
        return fullName;
    }
}
