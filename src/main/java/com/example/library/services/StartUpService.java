package com.example.library.services;

import com.example.library.GlobalFunctions;
import com.example.library.models.Block;
import com.example.library.models.Book;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    public BookService bookService;

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

    public void initDates()
    {
        Iterable< Book> books = bookService.getAll();
        for (Book book: books)
        {
            int days = getRandomNumber(-30, 0);
            book.setReceiptDate(utils.getDate(days, 0, 0, 0));
            bookService.save(book);
        }
    }

    public int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }
}
