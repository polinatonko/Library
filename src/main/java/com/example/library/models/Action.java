package com.example.library.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Timer;

@Getter
@Setter
@Entity
@Inheritance
@DiscriminatorColumn(name = "ACTION_TYPE")
public abstract class Action {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Integer id;
    @ManyToOne
    @JoinColumn(name = "user_id")
    protected User user;
    @Transient
    protected Timer timer;
}
