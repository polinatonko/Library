package com.example.library.services;

import com.example.library.GlobalFunctions;
import com.example.library.config.CustomUserDetails;
import com.example.library.enums.BookingStatus;
import com.example.library.models.*;
import com.example.library.repositories.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.*;

@Service
public class BookingService {
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private TimerService timerService;
    @Autowired
    private BookService bookService;
    @Autowired
    private GlobalFunctions utils;
    public List<Booking> getByPeriod(Date from, Date to)
    {
        List<Booking> bookings = bookingRepository.findByPeriod(from, to);
        List<Booking> res = new ArrayList<>();
        for (Booking booking: bookings)
            if (booking.isActive())
                res.add(booking);
        return res;
    }
    public List<Booking> getByPeriodAndUserId(Date from, Date to, Integer userId)
    {
        List<Booking> bookings = bookingRepository.findByPeriodAndUserId(from, to, userId);
        List<Booking> res = new ArrayList<>();
        for (Booking booking: bookings)
            if (booking.isActive())
                res.add(booking);
        return res;
    }
    public Iterable<Booking> getAll() { return bookingRepository.findAll(); }
    public Iterable<Booking> getUserBookings(Integer id) { return bookingRepository.findByUserIdAndIsActive(id, true); }

    public BookingStatus checkBooking(Integer editionId, Integer userId)
    {
        Optional<Booking> booking = bookingRepository.findByEdition_IdAndUser_IdAndIsActive(editionId, userId, true);
        boolean copiesExist = bookService.existsById(editionId) && bookService.getById(editionId).getCopiesCount() > 0;
        return copiesExist ? (booking.isPresent() ? BookingStatus.DISABLED : BookingStatus.ENABLED) : BookingStatus.ZERO_COPIES;
    }

    public Booking create(Edition edition, User user, Date lastDay, TimerTask task)
    {
        Booking booking = new Booking(edition, user, lastDay, utils.getCurentDate());
        bookingRepository.save(booking);
        booking.setTimer(timerService.getTimerById(user.getId(), booking.getId()), task);

        bookService.book((Book)edition);

        return booking;
    }

    public void cancelBooking(Integer id)
    {
        Booking activeBooking = getByIdOrThrowException(id);
        activeBooking.setActive(false);
        bookingRepository.save(activeBooking);

        bookService.unBook((Book)activeBooking.getEdition());

        timerService.stopTimerByUserId(activeBooking.getUser().getId(), activeBooking.getId());
    }

    public void cancelBooking(Integer editionId, Integer userId)
    {
        Optional<Booking> opt = bookingRepository.findByEdition_IdAndUser_IdAndIsActive(editionId, userId, true);
        if (opt.isEmpty())
            return;

        Booking booking = opt.get();
        cancelBooking(booking.getId());
    }

    public void checkPermission(CustomUserDetails userDetails, Integer id) throws AccessDeniedException
    {
        Optional<Booking> object = bookingRepository.findById(id);

        if (object.isEmpty())
            throw new IllegalArgumentException("Object with such id doesn't exist");

        if (userDetails == null || (!Objects.equals(userDetails.getId(), object.get().getUser().getId()) && !userDetails.isAdmin()))
            throw new AccessDeniedException("Access denied!");
    }

    private Booking getByIdOrThrowException(Integer id) throws IllegalArgumentException {
        Optional<Booking> object = bookingRepository.findById(id);

        if (object.isEmpty())
            throw new IllegalArgumentException("Object with such id doesn't exist");

        return object.get();
    }
}

