package com.example.library.services;

import com.example.library.GlobalFunctions;
import com.example.library.enums.IssuanceStatus;
import com.example.library.models.Book;
import com.example.library.models.Issuance;
import com.example.library.models.Publisher;
import com.example.library.repositories.IssuanceRepository;
import com.example.library.repositories.UserRepository;
import com.example.library.timer.BlockTimerTask;
import com.example.library.timer.IssuanceTimerTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class IssuanceService {
    @Autowired
    private IssuanceRepository issuanceRepository;
    @Autowired
    private BookService bookService;
    @Autowired
    private TimerService timerService;
    @Autowired
    private UserService userService;
    @Autowired
    private GlobalFunctions utils;
    public Issuance findByIds(Integer editionId, Integer userId) throws Exception {
        List<Issuance> i = issuanceRepository.findByUserIdAndEditionIdAndStatus(userId, editionId, IssuanceStatus.EXPIRED);
        Issuance issuance;
        if (i.isEmpty())
        {
            i = issuanceRepository.findByUserIdAndEditionIdAndStatus(userId, editionId, IssuanceStatus.ACTIVE);
            if (i.isEmpty())
            {
                throw new Exception("This issuance doesnt exist!");
            }
            issuance = i.getFirst();
        }
        else
            issuance = i.getFirst();
        return issuance;
    }
    public boolean exists(Integer editionId, Integer userId)
    {
        return issuanceRepository.existsByUserIdAndEditionIdAndStatus(userId, editionId, IssuanceStatus.ACTIVE);
        //return issuanceRepository.existsByUserIdAndEditionIdAndIsActive(userId, editionId, true);
    }
    public List<Issuance> getAllByPeriod(Date from, Date to) { return issuanceRepository.findByPeriod(from, to); }
    public List<Issuance> getAllByPeriod(Date from, Date to, Integer userId)
    {
        return issuanceRepository.findByPeriodAndUserId(from, to, userId);
    }
    public void expire(Issuance issuance)
    {
        issuance.setStatus(IssuanceStatus.EXPIRED);

        check_expires(issuance.getUser().getId());

        issuanceRepository.save(issuance);
    }
    public void cancel(Issuance issuance)
    {
        Book book = (Book)issuance.getEdition();
        book.incCopiesCount();
        bookService.save(book);

        issuance.setStatus((issuance.getStatus() == IssuanceStatus.EXPIRED) ? IssuanceStatus.RETURNED_EXPIRED : IssuanceStatus.RETURNED);
        check_expires(issuance.getUser().getId());

        //issuance.setActive(false);
        issuanceRepository.save(issuance);
    }
    public void delete(Issuance issuance)
    {
        Book book = (Book)issuance.getEdition();
        book.incCopiesCount();
        bookService.save(book);
        issuanceRepository.delete(issuance);
    }
    public void create(Issuance issuance, IssuanceTimerTask task)
    {
        issuanceRepository.save(issuance);
        Book book = (Book)issuance.getEdition();
        book.decCopiesCount();
        bookService.save(book);
        issuance.setTimer(timerService.getTimerById(issuance.getUser().getId(), issuance.getId()), task);
    }
    public void save(Issuance issuance) { issuanceRepository.save(issuance); }
    public Iterable<Issuance> getAll() { return issuanceRepository.findAll(); }
    public Iterable<Issuance> getByUserId(Integer userId) { return issuanceRepository.findByUserId(userId); }
    public Issuance getById(Integer id) { return getByIdOrThrowException(id); }
    private Issuance getByIdOrThrowException(Integer id) throws IllegalArgumentException {
        Optional<Issuance> issuance = issuanceRepository.findById(id);

        if (issuance.isEmpty())
            throw new IllegalArgumentException("Issuance with such id doesn't exist");

        return issuance.get();
    }
    private int getExpiredCount(Date from, Integer userId)
    {
        List<Issuance> iByPeriod = issuanceRepository.findByPeriodAndUserId(from, utils.getCurentDate(), userId);
        int count = 0;
        for (Issuance i : iByPeriod)
            if (i.getStatus() == IssuanceStatus.EXPIRED || i.getStatus() == IssuanceStatus.RETURNED_EXPIRED)
                count++;
        return count;
    }

    private void check_expires(Integer userId)
    {
        if (getExpiredCount(utils.getDate(-30*3, 0, 0, 0), userId) > 5)
            userService.blockUser(userId, utils.getDate(30, 0, 0, 0));
    }
}
