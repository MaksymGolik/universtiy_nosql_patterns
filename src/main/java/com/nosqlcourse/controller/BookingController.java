package com.nosqlcourse.controller;

import com.nosqlcourse.dto.BookingCreateRequest;
import com.nosqlcourse.dto.BookingItemCreateRequest;
import com.nosqlcourse.model.Booking;
import com.nosqlcourse.model.BookingItem;
import com.nosqlcourse.model.User;
import com.nosqlcourse.service.IBookingService;
import com.nosqlcourse.service.IUserService;
import com.nosqlcourse.service.impl.BookingService;
import com.nosqlcourse.service.impl.UserService;
import com.nosqlcourse.service.impl.proxy.BookingProxyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@RestController
public class BookingController {

    private final IUserService userService = new UserService();
    private final IBookingService bookingService = new BookingService();

    @GetMapping("/bookings")
    public List<Booking> viewBookingsByStatus(@RequestHeader("email") String email,
                                              @RequestHeader("password") String password,
                                              @RequestParam String bookingStatus){
        return new BookingProxyService((BookingService) bookingService, getUserByHeaderCredentials(email, password))
                .getBookingsByStatus(bookingStatus);
    }

    @PostMapping("/create_booking")
    ResponseEntity<?> createBooking(@RequestHeader("email") String email,
                                    @RequestHeader("password") String password,
                                    @RequestBody BookingCreateRequest bookingCreateRequest){
        BookingProxyService proxyService = new BookingProxyService((BookingService) bookingService, getUserByHeaderCredentials(email, password));
        Booking booking = BookingCreateRequest.toBooking(bookingCreateRequest);
        booking = booking.status(proxyService.getStatus("CREATED"));
        List<BookingItem> bookingItems = new ArrayList<>();
        bookingCreateRequest.getItems().forEach(item->bookingItems.add(BookingItemCreateRequest.toBookingItem(item)));
        Long bookingId = proxyService.createBooking(booking,bookingItems);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/bookings?bookingStatus=CREATED").buildAndExpand().toUri();

        return ResponseEntity.created(location).body(proxyService.getBookingById(bookingId));
    }

    @GetMapping("/booking/{id}")
    public Booking viewBooking(@RequestHeader("email") String email,
                               @RequestHeader("password") String password,
                               @PathVariable Long id){
        return new BookingProxyService((BookingService) bookingService, getUserByHeaderCredentials(email, password))
                .getBookingById(id);
    }

    @GetMapping("/booking/{id}/items")
    public List<BookingItem> getBookingItemsByBookingId(@RequestHeader("email") String email,
                                                        @RequestHeader("password") String password,
                                                        @PathVariable Long id){
        return new BookingProxyService((BookingService) bookingService, getUserByHeaderCredentials(email, password))
                .getBookingItemsByBookingId(id);
    }

    @GetMapping("/user/{id}/bookings")
    public List<Booking> getBookingsByUser(@RequestHeader("email") String email,
                                           @RequestHeader("password") String password,
                                           @PathVariable Long id){
        return new BookingProxyService((BookingService) bookingService, getUserByHeaderCredentials(email, password))
                .getBookingsByUserId(id);
    }

    @PatchMapping("/booking/{id}")
    public Booking updateStatus(@RequestHeader("email") String email,
                                @RequestHeader("password") String password,
                                @PathVariable Long id,
                                @RequestParam String statusName) {
        BookingProxyService proxyService = new BookingProxyService((BookingService) bookingService, getUserByHeaderCredentials(email, password));
        proxyService.changeBookingStatus(id,proxyService.getStatus(statusName));
        return proxyService.getBookingById(id);
    }

    @DeleteMapping("/booking/{id}")
    public ResponseEntity<?> deleteBooking(@RequestHeader("email") String email,
                                           @RequestHeader("password") String password,
                                           @PathVariable Long id){
        new BookingProxyService((BookingService) bookingService, getUserByHeaderCredentials(email, password))
                .deleteBookingById(id);
        return ResponseEntity.noContent().build();
    }

    private User getUserByHeaderCredentials(String email, String password){
        User user = userService.getUserByEmail(email);
        if(user.getPassword().equals(password)) return user;
        return null;
    }
}