package com.example.library.timer;

import com.example.library.models.Issuance;
import com.example.library.services.IssuanceService;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.TimerTask;

@NoArgsConstructor
public class IssuanceTimerTask extends TimerTask {
    Issuance issuance;
    @Autowired
    private IssuanceService issuanceService;
    public IssuanceTimerTask(Issuance issuance) {
        this.issuance = issuance;
    }
    @Override
    public void run() {
        issuanceService.expire(issuance);
    }
}
