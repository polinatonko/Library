package com.example.library.services;

import com.example.library.models.Book;
import com.example.library.models.Issuance;
import com.example.library.models.Publisher;
import com.example.library.repositories.IssuanceRepository;
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
    public Issuance findByIds(Integer editionId, Integer userId) { return issuanceRepository.findByUserIdAndEditionIdAndIsActive(userId, editionId, true);}
    public boolean exists(Integer editionId, Integer userId) { return issuanceRepository.existsByUserIdAndEditionIdAndIsActive(userId, editionId, true);}
    public Iterable<Issuance> getIssuances(Integer userId)
    {
        return issuanceRepository.findByUserId(userId);
    }
    public List<Issuance> getIssuancesByPeriod(Date from, Date to) { return issuanceRepository.findByPeriod(from, to); }
    public List<Issuance> getIssuancesByPeriod(Date from, Date to, Integer userId)
    {
        return issuanceRepository.findByPeriodAndUserId(from, to, userId);
    }
    public void cancel(Issuance issuance)
    {
        Book book = (Book)issuance.getEdition();
        book.incCopiesCount();
        bookService.save(book);
        issuance.setActive(false);
        issuanceRepository.save(issuance);
    }
    public void delete(Issuance issuance)
    {
        Book book = (Book)issuance.getEdition();
        book.incCopiesCount();
        bookService.save(book);
        issuanceRepository.delete(issuance);
    }
    public void create(Issuance issuance)
    {
        issuanceRepository.save(issuance);
        Book book = (Book)issuance.getEdition();
        book.decCopiesCount();
        bookService.save(book);
    }
    public void save(Issuance issuance) { issuanceRepository.save(issuance); }
    public Iterable<Issuance> getAll() { return issuanceRepository.findAll(); }
    public Issuance getById(Integer id) { return getByIdOrThrowException(id); }
    private Issuance getByIdOrThrowException(Integer id) throws IllegalArgumentException {
        Optional<Issuance> issuance = issuanceRepository.findById(id);

        if (issuance.isEmpty())
            throw new IllegalArgumentException("Issuance with such id doesn't exist");

        return issuance.get();
    }
}
