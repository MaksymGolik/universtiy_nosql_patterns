package com.nosqlcourse.dao;

public abstract class DAOFactory {

    private static DAOFactory instance;

    public abstract IUserDAO getUserDao();
    public abstract IHotelRoomDAO getHotelRoomDAO();
    public abstract IBookingDAO getBookingDAO();

    public static synchronized DAOFactory getDAOInstance(TypeDAO typeDAO){
        if(instance == null){
            switch (typeDAO){
                case MYSQL: instance =  new MySQLDAOFactory(); break;
                case MONGO: instance = new MongoDbDAOFactory(); break;
                default: throw new IllegalStateException("Database not supported");
            }
        }
        return instance;
    }
}
