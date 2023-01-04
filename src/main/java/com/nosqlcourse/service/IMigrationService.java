package com.nosqlcourse.service;

import com.nosqlcourse.dao.DAOFactory;
import com.nosqlcourse.model.HotelRoom;

import java.util.List;

public interface IMigrationService {
    void migrateHotelRooms(DAOFactory to, List<HotelRoom> rooms);
}
