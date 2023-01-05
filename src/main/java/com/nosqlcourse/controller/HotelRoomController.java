package com.nosqlcourse.controller;

import com.nosqlcourse.dto.RoomTypeCreateRequest;
import com.nosqlcourse.exception.DataNotFoundException;
import com.nosqlcourse.model.HotelRoom;
import com.nosqlcourse.model.User;
import com.nosqlcourse.service.IHotelRoomService;
import com.nosqlcourse.service.IUserService;
import com.nosqlcourse.service.impl.HotelRoomService;
import com.nosqlcourse.service.impl.UserService;
import com.nosqlcourse.service.impl.proxy.HotelRoomProxyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.sql.Date;
import java.util.List;

@RestController
public class HotelRoomController {
    private final IUserService userService = new UserService();
    private final IHotelRoomService hotelRoomService = new HotelRoomService();

    @GetMapping("/rooms")
    List<HotelRoom> getHotelRoomCatalog() throws DataNotFoundException {
        return hotelRoomService.getAllRoomsThanGroupByInfo();
    }

    @GetMapping("/room/{id}")
    HotelRoom getRoomById(@RequestHeader("email") String email,
                          @RequestHeader("password") String password,
                          @PathVariable Long id) throws DataNotFoundException {
        return new HotelRoomProxyService((HotelRoomService) hotelRoomService,
                getUserByHeaderCredentials(email,password))
                .getRoomById(id);

    }

    @GetMapping("/search_available")
    List<HotelRoom> getAvailableHotelRooms(@RequestHeader("email") String email,
                                           @RequestHeader("password") String password,
                                           @RequestParam String period,
                                           @RequestParam(required = false) String typeName,
                                           @RequestParam(required = false) Integer numberOfPeople) throws DataNotFoundException {
        final String[] dates = period.split(":");
        Date checkIn = Date.valueOf(dates[0].trim());
        Date checkOut = Date.valueOf(dates[1].trim());
        HotelRoomProxyService proxyService = new HotelRoomProxyService((HotelRoomService) hotelRoomService,getUserByHeaderCredentials(email,password));
        if(numberOfPeople!=null && typeName!=null ) return proxyService
                .getAllAvailableRoomsByDatesAndNumberOfPeopleAndType(checkIn,checkOut,numberOfPeople,typeName);
        if(typeName!=null) return proxyService
                .getAllAvailableRoomsByDatesAndType(checkIn,checkOut,typeName);
        if(numberOfPeople!=null) return proxyService
                .getAllAvailableRoomsByDatesAndNumberOfPeople(checkIn,checkOut,numberOfPeople);
        return proxyService.getAllAvailableRoomsByDates(Date.valueOf(dates[0].trim()),Date.valueOf(dates[1].trim()));
    }

    @GetMapping("/check_availability/{id}")
    boolean checkRoomAvailability(@RequestHeader("email") String email,
                                  @RequestHeader("password") String password,
            @RequestParam String period, @PathVariable Long id) throws DataNotFoundException {
        final String[] dates = period.split(":");
        return new HotelRoomProxyService((HotelRoomService) hotelRoomService,getUserByHeaderCredentials(email,password))
                .checkRoomAvailabilityByDates(id, Date.valueOf(dates[0].trim()), Date.valueOf(dates[1].trim()));
    }

    @PostMapping("/add_roomtype")
    ResponseEntity<?> createRoomType(@RequestHeader("email") String email,
                                     @RequestHeader("password") String password,
                                     @RequestBody RoomTypeCreateRequest roomTypeCreateRequest){
        new HotelRoomProxyService((HotelRoomService) hotelRoomService, getUserByHeaderCredentials(email,password))
                .insertRoomType(RoomTypeCreateRequest.toRoomType(roomTypeCreateRequest));
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/rooms").buildAndExpand().toUri();

        return ResponseEntity.created(location).body(roomTypeCreateRequest);
    }

    private User getUserByHeaderCredentials(String email, String password){
        User user = userService.getUserByEmail(email);
        if(user.getPassword().equals(password)) return user;
        return null;
    }
}