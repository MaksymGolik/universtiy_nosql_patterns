package com.nosqlcourse.controller;

import com.nosqlcourse.dao.DAOFactory;
import com.nosqlcourse.dao.TypeDAO;
import com.nosqlcourse.dto.RoomTypeCreateRequest;
import com.nosqlcourse.exception.DataNotFoundException;
import com.nosqlcourse.model.HotelRoom;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.sql.Date;
import java.util.List;

@RestController
public class HotelRoomController {

    private final DAOFactory dao = DAOFactory.getDAOInstance(TypeDAO.MONGO);

    @GetMapping("/rooms")
    List<HotelRoom> getHotelRoomCatalog() throws DataNotFoundException {
        return dao.getHotelRoomDAO().getAllRoomsThanGroupByInfo();
    }

    @GetMapping("/room/{id}")
    HotelRoom getRoomById(@PathVariable Long id) throws DataNotFoundException {
        return dao.getHotelRoomDAO().getRoomById(id);
    }

    @GetMapping("/search_available")
    List<HotelRoom> getAvailableHotelRooms(@RequestParam String period,
                                           @RequestParam(required = false) String typeName,
                                           @RequestParam(required = false) Integer numberOfPeople) throws DataNotFoundException {
        final String[] dates = period.split(":");
        Date checkIn = Date.valueOf(dates[0].trim());
        Date checkOut = Date.valueOf(dates[1].trim());
        if(numberOfPeople!=null && typeName!=null ) return dao.getHotelRoomDAO()
                .getAllAvailableRoomsByDatesAndNumberOfPeopleAndType(checkIn,checkOut,numberOfPeople,typeName);
        if(typeName!=null) return dao.getHotelRoomDAO()
                .getAllAvailableRoomsByDatesAndType(checkIn,checkOut,typeName);
        if(numberOfPeople!=null) return dao.getHotelRoomDAO()
                .getAllAvailableRoomsByDatesAndNumberOfPeople(checkIn,checkOut,numberOfPeople);
        return dao.getHotelRoomDAO().getAllAvailableRoomsByDates(Date.valueOf(dates[0].trim()),Date.valueOf(dates[1].trim()));
    }

    @GetMapping("/check_availability/{id}")
    boolean checkRoomAvailability(@RequestParam String period, @PathVariable Long id) throws DataNotFoundException {
        final String[] dates = period.split(":");
        return dao.getHotelRoomDAO().checkRoomAvailabilityByDates(id, Date.valueOf(dates[0].trim()), Date.valueOf(dates[1].trim()));
    }

    @PostMapping("/add_roomtype")
    ResponseEntity<?> createRoomType(@RequestBody RoomTypeCreateRequest roomTypeCreateRequest){
        dao.getHotelRoomDAO().insertRoomType(RoomTypeCreateRequest.toRoomType(roomTypeCreateRequest));

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/rooms").buildAndExpand().toUri();

        return ResponseEntity.created(location).body(roomTypeCreateRequest);
    }
}