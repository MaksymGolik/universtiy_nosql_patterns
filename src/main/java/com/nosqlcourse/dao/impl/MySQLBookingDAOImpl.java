package com.nosqlcourse.dao.impl;

import com.nosqlcourse.config.DataSourceConfiguration;
import com.nosqlcourse.dao.IBookingDAO;
import com.nosqlcourse.exception.DataNotFoundException;
import com.nosqlcourse.model.Booking;
import com.nosqlcourse.model.BookingItem;
import com.nosqlcourse.model.BookingStatus;
import com.nosqlcourse.model.HotelRoom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MySQLBookingDAOImpl implements IBookingDAO {

    private static final Logger log = LoggerFactory.getLogger(MySQLBookingDAOImpl.class);
    private static final DataSource dataSource = DataSourceConfiguration.getDataSource();

    private static final String INSERT_BOOKING = "INSERT INTO booking (guest_surname, " +
            " guest_name, guest_phone_number, create_time, last_update_time, user_id, status_id)" +
            " VALUES (?,?,?,now(),now(),?,?)";
    private static final String INSERT_BOOKING_ITEM = "INSERT INTO booking_has_hotel_room (booking_id, hotel_room_id," +
            " checkIn_date, checkOut_date) VALUES (?,?,?,?)";
    private static final String GET_STATUS = "SELECT * FROM booking_status WHERE name=?";
    private static final String GET_BOOKINGS_BY_USER_ID = "SELECT booking.id, guest_surname, guest_name, " +
            "guest_phone_number ,booking.price, create_time, last_update_time, user_id, status_id,name,description " +
            "FROM booking JOIN booking_status ON status_id=booking_status.id WHERE user_id=?  ORDER BY create_time DESC";
    private static final String GET_BOOKING_BY_ID = "SELECT booking.id, guest_surname, guest_name, " +
            "guest_phone_number ,booking.price, create_time, last_update_time, user_id, status_id,name, description " +
            "FROM booking JOIN booking_status ON status_id=booking_status.id WHERE booking.id=?";
    private static final String GET_BOOKING_ITEMS_BY_BOOKING_ID = "SELECT * FROM booking_has_hotel_room WHERE booking_id = ?";
    private static final String SET_BOOKING_STATUS = "UPDATE booking SET status_id = ? WHERE id = ?";
    private static final String GET_BOOKINGS_BY_STATUS = "SELECT booking.id, guest_surname, guest_name, " +
            "guest_phone_number ,booking.price, create_time, last_update_time, user_id, status_id,name, description " +
            "FROM booking JOIN booking_status ON status_id=booking_status.id WHERE name=?  ORDER BY create_time DESC";

    private static final String DELETE_BOOKING_BY_ID = "DELETE FROM booking WHERE id = ?";

    private Booking extractBooking(ResultSet resultSet) throws SQLException {
        BookingStatus status = new BookingStatus();
        status.setId(resultSet.getLong(9));
        status.setName(resultSet.getString(10));
        status.setDescription(resultSet.getString(11));
        return new Booking()
                .id(resultSet.getLong(1))
                .guestSurname(resultSet.getString(2))
                .guestName(resultSet.getString(3))
                .guestPhoneNumber(resultSet.getString(4))
                .price(resultSet.getDouble(5))
                .createTime(Timestamp.valueOf(resultSet.getString(6)))
                .lastUpdateTime(Timestamp.valueOf(resultSet.getString(7)))
                .userId(resultSet.getLong(8))
                .status(status);
    }

    private BookingItem extractBookingItem(ResultSet resultSet) throws SQLException {
        BookingItem item = new BookingItem();
        item.setBookingId(resultSet.getLong(1));
        item.setRoomId(resultSet.getLong(2));
        item.setCheckInDate(Date.valueOf(resultSet.getString(3)));
        item.setCheckOutDate(Date.valueOf(resultSet.getString(4)));
        item.setPrice(resultSet.getDouble(5));
        return item;
    }


    public Long createBooking(Booking booking,List<BookingItem> bookingItems) {
        long newId = -1;
        try(Connection connection = dataSource.getConnection()) {
            connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            connection.setAutoCommit(false);
            try(PreparedStatement preparedStatement =
                        connection.prepareStatement(INSERT_BOOKING, Statement.RETURN_GENERATED_KEYS)) {
                preparedStatement.setString(1, booking.getGuestSurname());
                preparedStatement.setString(2, booking.getGuestName());
                preparedStatement.setString(3, booking.getGuestPhoneNumber());
                preparedStatement.setLong(4, booking.getUserId());
                preparedStatement.setLong(5, booking.getStatus().getId());
                preparedStatement.executeUpdate();
                ResultSet key = preparedStatement.getGeneratedKeys();
                if (key.next()){
                    newId = key.getLong(1);
                    for(BookingItem bookingItem : bookingItems){
                        bookingItem.setBookingId(newId);
                        if(!insertBookingItem(connection, bookingItem)){
                            connection.rollback();
                            connection.setAutoCommit(true);
                            break;
                        }
                    }
                }
                else {
                    connection.rollback();
                    connection.setAutoCommit(true);
                }
            }
            connection.commit();
            connection.setAutoCommit(true);
        } catch (SQLException e){
            log.error(e.getMessage());
        }
        return newId;
    }


    private boolean insertBookingItem(Connection connection, BookingItem item) {
        int affectedRows = -1;
        try(PreparedStatement preparedStatement = connection.prepareStatement(INSERT_BOOKING_ITEM)){
            preparedStatement.setLong(1,item.getBookingId());
            preparedStatement.setLong(2,item.getRoomId());
            preparedStatement.setString(3,item.getCheckInDate().toString());
            preparedStatement.setString(4,item.getCheckOutDate().toString());
            affectedRows = preparedStatement.executeUpdate();
        } catch (SQLException e){
            log.error(e.getMessage());
        }
        return affectedRows == 1;
    }

    @Override
    public BookingStatus getStatus(String statusName) throws DataNotFoundException {
        try(Connection connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(GET_STATUS)) {
            preparedStatement.setString(1, statusName);
            try(ResultSet resultSet = preparedStatement.executeQuery()){
                if (resultSet.next()) {
                    BookingStatus bookingStatus = new BookingStatus();
                    bookingStatus.setId(resultSet.getLong(1));
                    bookingStatus.setName(resultSet.getString(2));
                    bookingStatus.setDescription(resultSet.getString(3));
                    return bookingStatus;
                } else throw new DataNotFoundException();
            }
        }
        catch (SQLException e){
            log.error(e.getMessage());
            throw new DataNotFoundException();
        }
    }

    @Override
    public List<Booking> getBookingsByUserId(long userId) throws DataNotFoundException {
        List<Booking> bookingList = new ArrayList<>();
        try(Connection connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(GET_BOOKINGS_BY_USER_ID)){
            preparedStatement.setLong(1,userId);
            try(ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    bookingList.add(extractBooking(resultSet));
                }
            }
            if(bookingList.size()==0)throw new DataNotFoundException();
        }catch (SQLException e){
            log.error(e.getMessage());
            throw new DataNotFoundException();
        }
        return bookingList;
    }

    public Booking getBookingById(long bookingId) throws DataNotFoundException {
        Booking booking = new Booking();
        try(Connection connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(GET_BOOKING_BY_ID)) {
            preparedStatement.setLong(1, bookingId);
            try(ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    booking = extractBooking(resultSet);
                } else throw new DataNotFoundException();
            }
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw new DataNotFoundException();
        }
        return booking;
    }

    @Override
    public List<BookingItem> getBookingItemsByBookingId(long bookingId) throws DataNotFoundException {
        List<BookingItem> bookingItems = new ArrayList<>();
        try(Connection connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(GET_BOOKING_ITEMS_BY_BOOKING_ID)){
            preparedStatement.setLong(1,bookingId);
            try(ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    bookingItems.add(extractBookingItem(resultSet));
                }
            }
            if(bookingItems.size()==0) throw new DataNotFoundException();
        }catch (SQLException e){
            log.error(e.getMessage());
            throw new DataNotFoundException();
        }
        return bookingItems;
    }

    @Override
    public boolean changeBookingStatus(long bookingId, BookingStatus status) {
        int affectedRows = -1;
        try(Connection connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(SET_BOOKING_STATUS)){
            preparedStatement.setLong(1,status.getId());
            preparedStatement.setLong(2,bookingId);
            affectedRows = preparedStatement.executeUpdate();
        }catch (SQLException e){
            log.error(e.getMessage());
        }
        return affectedRows == 1;
    }

    @Override
    public List<Booking> getBookingsByStatus(String statusName) throws DataNotFoundException {
        List<Booking> bookingList = new ArrayList<>();
        try(Connection connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(GET_BOOKINGS_BY_STATUS)){
            preparedStatement.setString(1,statusName);
            try(ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    bookingList.add(extractBooking(resultSet));
                }
            }
            if(bookingList.size()==0)throw new DataNotFoundException();
        }catch (SQLException e){
            log.error(e.getMessage());
            throw new DataNotFoundException();
        }
        return bookingList;
    }

    @Override
    public boolean deleteBookingById(long bookingId) {
        int affectedRows = -1;
        try(Connection connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(DELETE_BOOKING_BY_ID)){
            preparedStatement.setLong(1, bookingId);
            affectedRows = preparedStatement.executeUpdate();
        } catch (SQLException ex){
            log.error(ex.getMessage());
        }
        return affectedRows != -1;
    }
}