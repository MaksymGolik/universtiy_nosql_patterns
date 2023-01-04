package com.nosqlcourse.dao;

import com.nosqlcourse.dao.impl.MySQLBookingDAOImpl;
import com.nosqlcourse.dao.impl.MySQLHotelRoomDAOImpl;
import com.nosqlcourse.dao.impl.MySQLUserDAOImpl;

public class MySQLDAOFactory extends DAOFactory{

    @Override
    public IUserDAO getUserDao() {
        return new MySQLUserDAOImpl();
    }

    @Override
    public IHotelRoomDAO getHotelRoomDAO() {
        return new MySQLHotelRoomDAOImpl();
    }

    @Override
    public IBookingDAO getBookingDAO() {
        return new MySQLBookingDAOImpl();
    }
}