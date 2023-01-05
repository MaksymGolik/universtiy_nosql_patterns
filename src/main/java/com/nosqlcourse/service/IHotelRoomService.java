package com.nosqlcourse.service;

import com.nosqlcourse.exception.AccessDeniedException;
import com.nosqlcourse.exception.DataNotFoundException;
import com.nosqlcourse.model.HotelRoom;
import com.nosqlcourse.model.RoomInfo;
import com.nosqlcourse.model.RoomType;

import java.sql.Date;
import java.util.List;

public interface IHotelRoomService {
    List<HotelRoom> getAllRooms();
    List<HotelRoom> getAllRoomsThanGroupByInfo();
    List<HotelRoom> getAllRoomsByTypeAndCapacity(String roomType, int capacity);
    List<HotelRoom> getAllAvailableRoomsByDates(Date checkIn, Date checkOut);
    List<HotelRoom> getAllAvailableRoomsByDatesAndNumberOfPeople (Date checkIn, Date checkOut, int numberOfPeople);
    List<HotelRoom> getAllAvailableRoomsByDatesAndType(Date checkIn, Date checkOut, String roomType);
    List<HotelRoom> getAllAvailableRoomsByDatesAndNumberOfPeopleAndType (Date checkIn, Date checkOut, int numberOfPeople, String roomType);
    HotelRoom getRoomById (long roomId) throws DataNotFoundException, AccessDeniedException;
    boolean checkRoomAvailabilityByDates(long roomId, Date checkIn, Date checkOut);
    Long insertRoomType(RoomType roomType);
    Long insertRoomInfo(RoomInfo roomInfo);
    Long insertRoom (HotelRoom room);
    boolean updateRoomType(RoomType roomType);
    boolean updateRoomInfo(RoomInfo roomInfo);
    boolean updateRoom (HotelRoom room);
}
