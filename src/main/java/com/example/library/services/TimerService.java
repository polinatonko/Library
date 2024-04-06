package com.example.library.services;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Timer;

@Service
public class TimerService {
    private final HashMap<Integer, Timer> timers = new HashMap<>();

    public Timer getTimerById(Integer id)
    {
        Timer timer = new Timer();
        timers.put(id, timer);
        return timer;
    }

    public void stopTimerByUserId(Integer id)
    {
        Timer timer = timers.get(id);
        if (timer == null)
            throw new IllegalArgumentException("Timer doesn't exist!");

        timer.cancel();
        timer.purge();
    }
}
