package com.example.library.services;

import com.example.library.models.Action;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Timer;

@Service
public class TimerService {
    // userId actionId
    private final HashMap<Pair<Integer, Integer>, Timer> timers = new HashMap<>();

    public Timer getTimerById(Integer id, Integer actionId)
    {
        Timer timer = new Timer();
        timers.put(Pair.of(id, actionId), timer);
        return timer;
    }

    public void stopTimerByUserId(Integer id, Integer actionId)
    {
        Timer timer = timers.get(Pair.of(id, actionId));
        if (timer == null)
            throw new IllegalArgumentException("Timer doesn't exist!");

        timer.cancel();
        timer.purge();
    }
}
