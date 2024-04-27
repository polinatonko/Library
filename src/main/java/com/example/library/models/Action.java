package com.example.library.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
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
    protected Date day;
    @ManyToOne
    @JoinColumn(name = "user_id")
    protected User user;
    @ManyToOne
    @JoinColumn(name = "edition_id")
    protected Edition edition;
    @Transient
    protected Timer timer;
    @Transient
    public String getDiscriminatorValue() {
        DiscriminatorValue val = this.getClass().getAnnotation( DiscriminatorValue.class );
        return val == null ? null : val.value();
    }
    public abstract String getType();
}
