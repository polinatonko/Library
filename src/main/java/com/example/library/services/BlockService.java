package com.example.library.services;

import com.example.library.GlobalFunctions;
import com.example.library.models.Block;
import com.example.library.models.User;
import com.example.library.repositories.BlockRepository;
import com.example.library.timer.BlockTimerTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class BlockService {
    @Autowired
    private BlockRepository blockRepository;
    @Autowired
    private TimerService timerService;
    @Autowired
    private GlobalFunctions utils;
    public List<Block> getByPeriod(Date from, Date to) { return blockRepository.findByPeriod(from, to); }
    public List<Block> getByPeriodAndUserId(Date from, Date to, Integer userId) { return blockRepository.findByPeriodAndUserId(from, to, userId); }
    public Iterable<Block> getAll() { return blockRepository.findAll(); }
    public Iterable<Block> getActive() { return blockRepository.findAllByIsActive(true); }
    public Block create(User user, BlockTimerTask task)
    {
        Block block = new Block(user, utils.getCurentDate());
        block.setDay(utils.getCurentDate());
        blockRepository.save(block);
        block.setTimer(timerService.getTimerById(user.getId(), block.getId()), task);
        return block;
    }

    public void cancelBlock(Integer userId)
    {
        Block activeBlock = blockRepository.findByUserIdAndIsActive(userId, true);
        activeBlock.setActive(false);
        blockRepository.save(activeBlock);
        timerService.stopTimerByUserId(userId, activeBlock.getId());
    }

    public Block save(Block block) { return blockRepository.save(block); }
    public void restore(Block block, BlockTimerTask task)
    {
        block.setTimer(timerService.getTimerById(block.getUser().getId(), block.getId()), task);
    }
}
