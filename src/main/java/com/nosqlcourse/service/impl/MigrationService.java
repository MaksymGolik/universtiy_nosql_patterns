package com.nosqlcourse.service.impl;

import com.nosqlcourse.dao.DAOFactory;
import com.nosqlcourse.model.HotelRoom;
import com.nosqlcourse.model.RoomInfo;
import com.nosqlcourse.model.RoomType;
import com.nosqlcourse.service.IMigrationService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MigrationService implements IMigrationService {


    @Override
    public void migrateHotelRooms(DAOFactory to, List<HotelRoom> rooms) {
        Set<RoomType> types = new HashSet<>();
        rooms.forEach(hotelRoom -> types.add(hotelRoom.getInfo().getType()));
        for(RoomType type : types){
            type.setId(to.getHotelRoomDAO().insertRoomType(type));
        }
        rooms.forEach(hotelRoom -> hotelRoom.getInfo().getType().setId(
                types.stream().filter(type-> type.equals(hotelRoom.getInfo().getType())).findFirst().get().getId()));
        Set<RoomInfo> infos = new HashSet<>();
        rooms.forEach(hotelRoom -> infos.add(hotelRoom.getInfo()));
        for(RoomInfo info : infos){
            info.setId(to.getHotelRoomDAO().insertRoomInfo(info));
        }
        rooms.forEach(hotelRoom -> hotelRoom.getInfo().setId(infos.stream().filter(info->info.equals(hotelRoom.getInfo())).findFirst().get().getId()));
        for(HotelRoom room : rooms){
            to.getHotelRoomDAO().insertRoom(room);
        }
    }
}