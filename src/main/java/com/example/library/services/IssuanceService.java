package com.example.library.services;

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
    public Issuance findByIds(Integer editionId, Integer userId) {
        Issuance res = issuanceRepository.findByUserIdAndEditionIdAndStatus(userId, editionId, IssuanceStatus.ACTIVE);
        if (res == null)
            res = issuanceRepository.findByUserIdAndEditionIdAndStatus(userId, editionId, IssuanceStatus.EXPIRED);
        return res;
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
        issuanceRepository.save(issuance);
    }
    public void cancel(Issuance issuance)
    {
        Book book = (Book)issuance.getEdition();
        book.incCopiesCount();
        bookService.save(book);
        issuance.setStatus(IssuanceStatus.RETURNED);
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
}
