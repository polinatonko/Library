package com.example.library.services;

import com.example.library.models.Action;
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
    public List<Block> getByPeriod(Date from, Date to) { return blockRepository.findByPeriod(from, to); }
    public List<Block> getByPeriodAndUserId(Date from, Date to, Integer userId) { return blockRepository.findByPeriodAndUserId(from, to, userId); }
    public Iterable<Block> getAll() { return blockRepository.findAll(); }
    public Block create(User user, BlockTimerTask task)
    {
        Block block = new Block(user);
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
}
