package com.example.library.models;

import com.example.library.timer.BlockTimerTask;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.Timer;

@Getter
@Setter
@NoArgsConstructor
@Entity
@DiscriminatorValue("B")
public class Block extends Action {
    private boolean isActive;

    public Block(User user, Date day) {
        this.user = user;
        this.isActive = true;
        this.day = day;
    }

    public void setTimer(Timer timer, BlockTimerTask task)
    {
        this.timer = timer;
        timer.schedule(task, user.getBlockingEnd());
    }

    @Override
    public String getType() {
        return this.getClass().getName();
    }
}
