package com.example.library.models;

import com.example.library.timer.BlockTimerTask;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Timer;

@Getter
@Setter
@NoArgsConstructor
@Entity
@DiscriminatorValue("B")
public class Block extends Action {
    private boolean isActive;

    public Block(User user, Timer timer, BlockTimerTask task) {
        this.user = user;
        this.isActive = true;
        this.timer = timer;

        timer.schedule(task, user.getBlockingEnd());
    }
}
