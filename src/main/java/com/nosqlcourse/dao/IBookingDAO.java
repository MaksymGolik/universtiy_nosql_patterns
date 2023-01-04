package com.nosqlcourse.dao;

import com.nosqlcourse.exception.DataNotFoundException;
import com.nosqlcourse.model.Booking;
import com.nosqlcourse.model.BookingItem;
import com.nosqlcourse.model.BookingStatus;

import java.util.List;

public interface IBookingDAO {
    Long createBooking (Booking booking, List<BookingItem> bookingItems);
    BookingStatus getStatus(String statusName) throws DataNotFoundException;
    List<Booking> getBookingsByUserId (long userId) throws DataNotFoundException;
    Booking getBookingById (long bookingId) throws DataNotFoundException;
    List<BookingItem> getBookingItemsByBookingId (long bookingId) throws DataNotFoundException;
    boolean changeBookingStatus (long bookingId, BookingStatus status);
    List<Booking> getBookingsByStatus (String statusName) throws DataNotFoundException;
    boolean deleteBookingById(long bookingId);
}
