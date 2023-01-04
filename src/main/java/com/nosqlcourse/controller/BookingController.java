package com.nosqlcourse.controller;

import com.nosqlcourse.dao.DAOFactory;
import com.nosqlcourse.dao.TypeDAO;
import com.nosqlcourse.dto.BookingCreateRequest;
import com.nosqlcourse.dto.BookingItemCreateRequest;
import com.nosqlcourse.exception.DataNotFoundException;
import com.nosqlcourse.model.Booking;
import com.nosqlcourse.model.BookingItem;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@RestController
public class BookingController {

    private final DAOFactory dao = DAOFactory.getDAOInstance(TypeDAO.MONGO);

    @GetMapping("/bookings")
    public List<Booking> viewBookingsByStatus(@RequestParam String bookingStatus) throws DataNotFoundException {
        return dao.getBookingDAO().getBookingsByStatus(bookingStatus);
    }

    @PostMapping("/create_booking")
    ResponseEntity<?> createBooking(@RequestBody BookingCreateRequest bookingCreateRequest) throws DataNotFoundException {
        Booking booking = BookingCreateRequest.toBooking(bookingCreateRequest);
        booking = booking.status(dao.getBookingDAO().getStatus("CREATED"));
        List<BookingItem> bookingItems = new ArrayList<>();
        bookingCreateRequest.getItems().forEach(item->bookingItems.add(BookingItemCreateRequest.toBookingItem(item)));
        Long bookingId = dao.getBookingDAO().createBooking(booking,bookingItems);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/bookings?bookingStatus=CREATED").buildAndExpand().toUri();

        return ResponseEntity.created(location).body(dao.getBookingDAO().getBookingById(bookingId));
    }

    @GetMapping("/booking/{id}")
    public Booking viewBooking(@PathVariable Long id) throws DataNotFoundException {
        return dao.getBookingDAO().getBookingById(id);
    }

    @GetMapping("/booking/{id}/items")
    public List<BookingItem> getBookingItemsByBookingId(@PathVariable Long id) throws DataNotFoundException{
        return dao.getBookingDAO().getBookingItemsByBookingId(id);
    }

    @GetMapping("/user/{id}/bookings")
    public List<Booking> getBookingsByUser(@PathVariable Long id) throws DataNotFoundException {
        return dao.getBookingDAO().getBookingsByUserId(id);
    }

    @PatchMapping("/booking/{id}")
    public Booking updateStatus(@PathVariable Long id, @RequestParam String statusName) throws DataNotFoundException {
        dao.getBookingDAO().changeBookingStatus(id,dao.getBookingDAO().getStatus(statusName));
        return dao.getBookingDAO().getBookingById(id);
    }

    @DeleteMapping("/booking/{id}")
    public ResponseEntity<?> deleteBooking(@PathVariable Long id){
        dao.getBookingDAO().deleteBookingById(id);
        return ResponseEntity.noContent().build();
    }
}
