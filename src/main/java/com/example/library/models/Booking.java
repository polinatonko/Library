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
@DiscriminatorValue("BOOK")
public class Booking extends Action {
    private boolean isActive;
    private Date lastDay;
    public Booking(Edition edition, User user, Date lastDay)
    {
        this.edition = edition;
        this.user = user;
        this.isActive = true;
        this.lastDay = lastDay;
    }

    public void setTimer(Timer timer, BlockTimerTask task)
    {
        this.timer = timer;
        timer.schedule(task, lastDay);
    }
    @Override
    public String getType() {
        return this.getClass().getName();
    }
}
