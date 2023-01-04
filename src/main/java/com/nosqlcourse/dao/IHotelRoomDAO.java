package com.nosqlcourse.dao;

import com.nosqlcourse.exception.DataNotFoundException;
import com.nosqlcourse.model.HotelRoom;
import com.nosqlcourse.model.RoomInfo;
import com.nosqlcourse.model.RoomType;

import java.sql.Date;
import java.util.List;

public interface IHotelRoomDAO {
    List<HotelRoom> getAllRooms() throws DataNotFoundException;
    List<HotelRoom> getAllRoomsThanGroupByInfo() throws DataNotFoundException;
    List<HotelRoom> getAllRoomsByTypeAndCapacity(String roomType, int capacity) throws DataNotFoundException;
    List<HotelRoom> getAllAvailableRoomsByDates(Date checkIn, Date checkOut) throws DataNotFoundException;
    List<HotelRoom> getAllAvailableRoomsByDatesAndNumberOfPeople (Date checkIn, Date checkOut, int numberOfPeople) throws DataNotFoundException;
    List<HotelRoom> getAllAvailableRoomsByDatesAndType(Date checkIn, Date checkOut, String roomType) throws DataNotFoundException;
    List<HotelRoom> getAllAvailableRoomsByDatesAndNumberOfPeopleAndType (Date checkIn, Date checkOut, int numberOfPeople, String roomType) throws DataNotFoundException;
    HotelRoom getRoomById (long roomId) throws DataNotFoundException;
    boolean checkRoomAvailabilityByDates(long roomId, Date checkIn, Date checkOut) throws DataNotFoundException;
    Long insertRoomType(RoomType roomType);
    Long insertRoomInfo(RoomInfo roomInfo);
    Long insertRoom (HotelRoom room);
    boolean updateRoomType(RoomType roomType);
    boolean updateRoomInfo(RoomInfo roomInfo);
    boolean updateRoom (HotelRoom room);
}
