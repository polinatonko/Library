package com.example.library.services;

import com.example.library.GlobalFunctions;
import com.example.library.models.Block;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class StartUpService {
    @Autowired
    public BlockService blockService;
    @Autowired
    public GlobalFunctions utils;
    @Autowired
    public UserService userService;

    @PostConstruct
    public void init() {
        restoreBlocks();
    }
    public void restoreBlocks() {
        for (Block block: blockService.getActive()) {
            if (block.getUser().getBlockingEnd().before(utils.getCurentDate()))
                userService.unblockUser(block.getUser().getId());
            else
                userService.restoreBlock(block.getUser().getId(), block);
        }
    }
}
