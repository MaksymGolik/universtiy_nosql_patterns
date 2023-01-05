package com.nosqlcourse.service.impl.proxy;

import com.nosqlcourse.exception.AccessDeniedException;
import com.nosqlcourse.exception.DataNotFoundException;
import com.nosqlcourse.model.Booking;
import com.nosqlcourse.model.BookingItem;
import com.nosqlcourse.model.BookingStatus;
import com.nosqlcourse.model.User;
import com.nosqlcourse.service.IBookingService;
import com.nosqlcourse.service.impl.BookingService;

import java.util.List;

public class BookingProxyService implements IBookingService {

    private final BookingService bookingService;
    private final User initiator;

    public BookingProxyService(BookingService bookingService, User initiator) {
        this.bookingService = bookingService;
        this.initiator = initiator;
    }

    @Override
    public Long createBooking(Booking booking, List<BookingItem> bookingItems){
        if(initiator!=null && booking.getUserId().equals(initiator.getId())){
            return bookingService.createBooking(booking, bookingItems);
        } else throw new AccessDeniedException("Access denied in createBooking() for initiator");
    }

    @Override
    public BookingStatus getStatus(String statusName){
        if(initiator!=null){
            return bookingService.getStatus(statusName);
        } else throw new AccessDeniedException("Access denied in getStatus() for unauthorized initiator");
    }

    @Override
    public List<Booking> getBookingsByUserId(long userId){
        if(initiator!=null){
            return bookingService.getBookingsByUserId(userId);
        } else throw new AccessDeniedException("Access denied in getBookingsByUserId() for unauthorized initiator");
    }

    @Override
    public Booking getBookingById(long bookingId){
        if(initiator!=null){
            return bookingService.getBookingById(bookingId);
        } else throw new AccessDeniedException("Access denied in getBookingById() for unauthorized initiator");
    }

    @Override
    public List<BookingItem> getBookingItemsByBookingId(long bookingId){
        if(initiator!=null){
            return bookingService.getBookingItemsByBookingId(bookingId);
        } else throw new AccessDeniedException("Access denied in getBookingItemsByBookingId() for unauthorized initiator");
    }

    @Override
    public boolean changeBookingStatus(long bookingId, BookingStatus status){
        if(initiator.getRole().getName().equals(ProxyAccess.ADMIN_ROLE_NAME)){
            return bookingService.changeBookingStatus(bookingId, status);
        } else throw new AccessDeniedException("Access denied in changeBookingStatus() for initiator " + initiator.getEmail());
    }

    @Override
    public List<Booking> getBookingsByStatus(String statusName){
        if(initiator!=null){
            return bookingService.getBookingsByStatus(statusName);
        } else throw new AccessDeniedException("Access denied in getBookingItemsByBookingId() for unauthorized initiator");
    }

    @Override
    public boolean deleteBookingById(long bookingId){
        if(initiator.getRole().getName().equals(ProxyAccess.ADMIN_ROLE_NAME)){
            return bookingService.deleteBookingById(bookingId);
        } else throw new AccessDeniedException("Access denied in deleteBookingById() for initiator " + initiator.getEmail());
    }
}
