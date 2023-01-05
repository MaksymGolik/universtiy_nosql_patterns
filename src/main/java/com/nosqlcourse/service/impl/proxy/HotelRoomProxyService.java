package com.nosqlcourse.service.impl.proxy;

import com.nosqlcourse.exception.AccessDeniedException;
import com.nosqlcourse.exception.DataNotFoundException;
import com.nosqlcourse.model.HotelRoom;
import com.nosqlcourse.model.RoomInfo;
import com.nosqlcourse.model.RoomType;
import com.nosqlcourse.model.User;
import com.nosqlcourse.service.IHotelRoomService;
import com.nosqlcourse.service.impl.HotelRoomService;

import java.sql.Date;
import java.util.List;

public class HotelRoomProxyService implements IHotelRoomService {

    private final HotelRoomService hotelRoomService;
    private final User initiator;

    public HotelRoomProxyService(HotelRoomService hotelRoomService, User initiator){
        this.hotelRoomService = hotelRoomService;
        this.initiator = initiator;
    }

    @Override
    public List<HotelRoom> getAllRooms(){
        if(initiator!=null){
            return hotelRoomService.getAllRooms();
        } else throw new AccessDeniedException("Access denied in getAllRooms() for unauthorized initiator");
    }

    @Override
    public List<HotelRoom> getAllRoomsThanGroupByInfo(){
        if(initiator!=null){
            return hotelRoomService.getAllRoomsThanGroupByInfo();
        } else throw new AccessDeniedException("Access denied in getAllRoomsThanGroupByInfo() for unauthorized initiator");
    }

    @Override
    public List<HotelRoom> getAllRoomsByTypeAndCapacity(String roomType, int capacity){
        if(initiator!=null){
            return hotelRoomService.getAllRoomsByTypeAndCapacity(roomType, capacity);
        } else throw new AccessDeniedException("Access denied in getAllRoomsByTypeAndCapacity() for unauthorized initiator");
    }

    @Override
    public List<HotelRoom> getAllAvailableRoomsByDates(Date checkIn, Date checkOut){
        if(initiator!=null){
            return hotelRoomService.getAllAvailableRoomsByDates(checkIn, checkOut);
        } else throw new AccessDeniedException("Access denied in getAllAvailableRoomsByDates() for unauthorized initiator");
    }

    @Override
    public List<HotelRoom> getAllAvailableRoomsByDatesAndNumberOfPeople(Date checkIn, Date checkOut, int numberOfPeople){
        if(initiator!=null){
            return hotelRoomService.getAllAvailableRoomsByDatesAndNumberOfPeople(checkIn, checkOut, numberOfPeople);
        } else throw new AccessDeniedException("Access denied in getAllAvailableRoomsByDatesAndNumberOfPeople() for unauthorized initiator");
    }

    @Override
    public List<HotelRoom> getAllAvailableRoomsByDatesAndType(Date checkIn, Date checkOut, String roomType){
        if(initiator!=null){
            return hotelRoomService.getAllAvailableRoomsByDatesAndType(checkIn, checkOut, roomType);
        } else throw new AccessDeniedException("Access denied in getAllAvailableRoomsByDatesAndType() for unauthorized initiator");
    }

    @Override
    public List<HotelRoom> getAllAvailableRoomsByDatesAndNumberOfPeopleAndType(Date checkIn, Date checkOut, int numberOfPeople, String roomType){
        if(initiator!=null){
            return hotelRoomService.getAllAvailableRoomsByDatesAndNumberOfPeopleAndType(checkIn, checkOut, numberOfPeople, roomType);
        } else throw new AccessDeniedException("Access denied in getAllAvailableRoomsByDatesAndNumberOfPeopleAndType() for unauthorized initiator");
    }

    @Override
    public HotelRoom getRoomById(long roomId){
        if(initiator!=null){
            return hotelRoomService.getRoomById(roomId);
        } else throw new AccessDeniedException("Access denied in getRoomById() for unauthorized initiator");
    }

    @Override
    public boolean checkRoomAvailabilityByDates(long roomId, Date checkIn, Date checkOut){
        if(initiator!=null){
            return hotelRoomService.checkRoomAvailabilityByDates(roomId, checkIn, checkOut);
        } else throw new AccessDeniedException("Access denied in checkRoomAvailabilityByDates() for unauthorized initiator");
    }

    @Override
    public Long insertRoomType(RoomType roomType){
        if(initiator.getRole().getName().equals(ProxyAccess.ADMIN_ROLE_NAME)){
            return hotelRoomService.insertRoomType(roomType);
        } else throw new AccessDeniedException("Access denied in insertRoomType() for initiator " +initiator.getEmail());
    }

    @Override
    public Long insertRoomInfo(RoomInfo roomInfo){
        if(initiator.getRole().getName().equals(ProxyAccess.ADMIN_ROLE_NAME)){
            return hotelRoomService.insertRoomInfo(roomInfo);
        } else throw new AccessDeniedException("Access denied in insertRoomInfo() for initiator " +initiator.getEmail());
    }

    @Override
    public Long insertRoom(HotelRoom room){
        if(initiator.getRole().getName().equals(ProxyAccess.ADMIN_ROLE_NAME)){
            return hotelRoomService.insertRoom(room);
        } else throw new AccessDeniedException("Access denied in insertRoom() for initiator " +initiator.getEmail());
    }

    @Override
    public boolean updateRoomType(RoomType roomType){
        if(initiator.getRole().getName().equals(ProxyAccess.ADMIN_ROLE_NAME)){
            return hotelRoomService.updateRoomType(roomType);
        } else throw new AccessDeniedException("Access denied in updateRoomType() for initiator " +initiator.getEmail());
    }

    @Override
    public boolean updateRoomInfo(RoomInfo roomInfo){
        if(initiator.getRole().getName().equals(ProxyAccess.ADMIN_ROLE_NAME)){
            return hotelRoomService.updateRoomInfo(roomInfo);
        } else throw new AccessDeniedException("Access denied in updateRoomInfo() for initiator " +initiator.getEmail());
    }

    @Override
    public boolean updateRoom(HotelRoom room){
        if(initiator.getRole().getName().equals(ProxyAccess.ADMIN_ROLE_NAME)){
            return hotelRoomService.updateRoom(room);
        } else throw new AccessDeniedException("Access denied in updateRoom() for initiator " +initiator.getEmail());
    }
}
