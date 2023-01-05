package com.nosqlcourse.service.impl;

import com.nosqlcourse.dao.DAOFactory;
import com.nosqlcourse.dao.IBookingDAO;
import com.nosqlcourse.dao.TypeDAO;
import com.nosqlcourse.exception.DataNotFoundException;
import com.nosqlcourse.model.Booking;
import com.nosqlcourse.model.BookingItem;
import com.nosqlcourse.model.BookingStatus;
import com.nosqlcourse.service.IBookingService;

import java.util.List;

public class BookingService implements IBookingService {

    private final IBookingDAO dao = DAOFactory.getDAOInstance(TypeDAO.MONGO).getBookingDAO();

    @Override
    public Long createBooking(Booking booking, List<BookingItem> bookingItems) {
        return dao.createBooking(booking, bookingItems);
    }

    @Override
    public BookingStatus getStatus(String statusName){
        return dao.getStatus(statusName);
    }

    @Override
    public List<Booking> getBookingsByUserId(long userId){
        return dao.getBookingsByUserId(userId);
    }

    @Override
    public Booking getBookingById(long bookingId){
        return dao.getBookingById(bookingId);
    }

    @Override
    public List<BookingItem> getBookingItemsByBookingId(long bookingId){
        return dao.getBookingItemsByBookingId(bookingId);
    }

    @Override
    public boolean changeBookingStatus(long bookingId, BookingStatus status) {
        return dao.changeBookingStatus(bookingId,status);
    }

    @Override
    public List<Booking> getBookingsByStatus(String statusName){
        return dao.getBookingsByStatus(statusName);
    }

    @Override
    public boolean deleteBookingById(long bookingId) {
        return dao.deleteBookingById(bookingId);
    }
}
