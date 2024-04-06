package com.example.library.timer;

import com.example.library.models.User;
import com.example.library.services.UserService;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.TimerTask;

@NoArgsConstructor
public class BlockTimerTask extends TimerTask {
    private User user;
    @Autowired
    private UserService userService;
    public BlockTimerTask(User user)
    {
        this.user = user;
    }

    @Override
    public void run() {
        userService.unblockUser(user.getId());
    }
}
