package com.nosqlcourse.service;

import com.nosqlcourse.exception.AccessDeniedException;
import com.nosqlcourse.exception.DataNotFoundException;
import com.nosqlcourse.model.Booking;
import com.nosqlcourse.model.BookingItem;
import com.nosqlcourse.model.BookingStatus;

import java.util.List;

public interface IBookingService {
    Long createBooking (Booking booking, List<BookingItem> bookingItems);
    BookingStatus getStatus(String statusName);
    List<Booking> getBookingsByUserId (long userId);
    Booking getBookingById (long bookingId);
    List<BookingItem> getBookingItemsByBookingId (long bookingId);
    boolean changeBookingStatus (long bookingId, BookingStatus status);
    List<Booking> getBookingsByStatus (String statusName);
    boolean deleteBookingById(long bookingId);
}
