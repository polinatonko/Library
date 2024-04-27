package com.example.library.services;

import com.example.library.enums.BookingStatus;
import com.example.library.models.*;
import com.example.library.repositories.BookingRepository;
import com.example.library.timer.BlockTimerTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class BookingService {
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private TimerService timerService;
    @Autowired
    private BookService bookService;
    public List<Booking> getByPeriod(Date from, Date to) { return bookingRepository.findByPeriod(from, to); }
    public List<Booking> getByPeriodAndUserId(Date from, Date to, Integer userId) { return bookingRepository.findByPeriodAndUserId(from, to, userId); }
    public Iterable<Booking> getAll() { return bookingRepository.findAll(); }
    public Iterable<Booking> getUserBookings(Integer id) { return bookingRepository.findByUserIdAndIsActive(id, true); }

    public BookingStatus checkBooking(Integer editionId, Integer userId)
    {
        Optional<Booking> booking = bookingRepository.findByEditionIdAndUserIdAndIsActive(editionId, userId, true);
        boolean copiesExist = bookService.existsById(editionId) && bookService.getById(editionId).getCopiesCount() > 0;
        return copiesExist ? (booking.isPresent() ? BookingStatus.DISABLED : BookingStatus.ENABLED) : BookingStatus.ZERO_COPIES;
    }

    public Booking create(Edition edition, User user, Date lastDay, BlockTimerTask task)
    {
        Booking booking = new Booking(edition, user, lastDay);
        bookingRepository.save(booking);
        booking.setTimer(timerService.getTimerById(user.getId(), booking.getId()), task);

        bookService.book((Book)edition);

        return booking;
    }

    public void cancelBooking(Integer editionId, Integer userId)
    {
        Booking activeBooking = bookingRepository.findByEditionIdAndUserIdAndIsActive(editionId, userId, true).get();
        activeBooking.setActive(false);
        bookingRepository.save(activeBooking);

        bookService.unBook((Book)activeBooking.getEdition());

        timerService.stopTimerByUserId(userId, activeBooking.getId());
    }
}
