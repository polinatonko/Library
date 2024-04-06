package com.example.library.services;

import com.example.library.models.Block;
import com.example.library.repositories.BlockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BlockService {
    @Autowired
    private BlockRepository blockRepository;

    public void cancelBlock(Integer userId)
    {
        Block activeBlock = blockRepository.findByUserIdAndIsActive(userId, true);
        activeBlock.setActive(false);
        blockRepository.save(activeBlock);
    }

    public Block save(Block block) { return blockRepository.save(block); }
}
