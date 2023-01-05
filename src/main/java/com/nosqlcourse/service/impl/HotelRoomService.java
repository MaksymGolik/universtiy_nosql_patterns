package com.nosqlcourse.service.impl;

import com.nosqlcourse.dao.DAOFactory;
import com.nosqlcourse.dao.IHotelRoomDAO;
import com.nosqlcourse.dao.TypeDAO;
import com.nosqlcourse.exception.DataNotFoundException;
import com.nosqlcourse.model.HotelRoom;
import com.nosqlcourse.model.RoomInfo;
import com.nosqlcourse.model.RoomType;
import com.nosqlcourse.service.IHotelRoomService;

import java.sql.Date;
import java.util.List;

public class HotelRoomService implements IHotelRoomService {

    private final IHotelRoomDAO dao = DAOFactory.getDAOInstance(TypeDAO.MONGO).getHotelRoomDAO();

    @Override
    public List<HotelRoom> getAllRooms(){
        return dao.getAllRooms();
    }

    @Override
    public List<HotelRoom> getAllRoomsThanGroupByInfo(){
        return dao.getAllRoomsThanGroupByInfo();
    }

    @Override
    public List<HotelRoom> getAllRoomsByTypeAndCapacity(String roomType, int capacity){
        return dao.getAllRoomsByTypeAndCapacity(roomType, capacity);
    }

    @Override
    public List<HotelRoom> getAllAvailableRoomsByDates(Date checkIn, Date checkOut){
        return dao.getAllAvailableRoomsByDates(checkIn, checkOut);
    }

    @Override
    public List<HotelRoom> getAllAvailableRoomsByDatesAndNumberOfPeople(Date checkIn, Date checkOut, int numberOfPeople){
        return dao.getAllAvailableRoomsByDatesAndNumberOfPeople(checkIn, checkOut, numberOfPeople);
    }

    @Override
    public List<HotelRoom> getAllAvailableRoomsByDatesAndType(Date checkIn, Date checkOut, String roomType){
        return dao.getAllAvailableRoomsByDatesAndType(checkIn, checkOut, roomType);
    }

    @Override
    public List<HotelRoom> getAllAvailableRoomsByDatesAndNumberOfPeopleAndType(Date checkIn, Date checkOut, int numberOfPeople, String roomType){
        return dao.getAllAvailableRoomsByDatesAndNumberOfPeopleAndType(checkIn, checkOut, numberOfPeople, roomType);
    }

    @Override
    public HotelRoom getRoomById(long roomId){
        return dao.getRoomById(roomId);
    }

    @Override
    public boolean checkRoomAvailabilityByDates(long roomId, Date checkIn, Date checkOut){
        return dao.checkRoomAvailabilityByDates(roomId, checkIn, checkOut);
    }

    @Override
    public Long insertRoomType(RoomType roomType) {
        return dao.insertRoomType(roomType);
    }

    @Override
    public Long insertRoomInfo(RoomInfo roomInfo) {
        return dao.insertRoomInfo(roomInfo);
    }

    @Override
    public Long insertRoom(HotelRoom room) {
        return dao.insertRoom(room);
    }

    @Override
    public boolean updateRoomType(RoomType roomType) {
        return dao.updateRoomType(roomType);
    }

    @Override
    public boolean updateRoomInfo(RoomInfo roomInfo) {
        return dao.updateRoomInfo(roomInfo);
    }

    @Override
    public boolean updateRoom(HotelRoom room) {
        return dao.updateRoom(room);
    }
}
