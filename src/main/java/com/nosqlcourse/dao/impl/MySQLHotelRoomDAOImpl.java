package com.nosqlcourse.dao.impl;

import com.nosqlcourse.config.DataSourceConfiguration;
import com.nosqlcourse.dao.IHotelRoomDAO;
import com.nosqlcourse.exception.DataNotFoundException;
import com.nosqlcourse.model.HotelRoom;
import com.nosqlcourse.model.RoomInfo;
import com.nosqlcourse.model.RoomType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Repository
public class MySQLHotelRoomDAOImpl implements IHotelRoomDAO {

    private static final Logger log = LoggerFactory.getLogger(MySQLHotelRoomDAOImpl.class);
    private static final DataSource dataSource = DataSourceConfiguration.getDataSource();

    private static final String GET_ALL_ROOMS = "SELECT hotel_room.id, room_info.id, capacity, price, room_info.description, image_url, room_type.id, name,room_type.description FROM hotel_room JOIN room_info on room_info_id=room_info.id JOIN room_type ON type_id=room_type.id";

    private static final String GET_ALL_ROOMS_GROUP_BY_ROOM_INFO = "SELECT hotel_room.id, room_info.id, capacity, price, room_info.description, image_url, room_type.id, name,room_type.description FROM hotel_room JOIN room_info on room_info_id=room_info.id JOIN room_type ON type_id=room_type.id GROUP BY room_info.id";
    public static final String GET_ALL_ROOMS_BY_TYPE_AND_CAPACITY_GROUP_BY_ROOM_INFO =
            "SELECT hotel_room.id, room_info.id, capacity, price, room_info.description, image_url, room_type.id, name,room_type.description " +
            "FROM hotel_room JOIN room_info on room_info_id=room_info.id JOIN room_type ON type_id=room_type.id " +
            "WHERE name=? AND capacity>=? GROUP BY room_info.id";
    private static final String CALL_PROCEDURE_GET_ALL_AVAILABLE_ROOMS_BY_DATES = "CALL searchAvailableRoomsByDates (?,?)";
    private static final String CALL_PROCEDURE_GET_ALL_AVAILABLE_ROOMS_BY_DATES_AND_NUMBER_OF_PEOPLE = "CALL searchAvailableRoomsByDatesAndNumberOfPeople (?,?,?)";
    private static final String CALL_PROCEDURE_GET_ALL_AVAILABLE_ROOMS_BY_DATES_AND_ROOM_TYPE = "CALL searchAvailableRoomsByDatesAndRoomType (?,?,?)";
    private static final String CALL_PROCEDURE_GET_ALL_AVAILABLE_ROOMS_BY_DATES_AND_NUMBER_OF_PEOPLE_AND_ROOM_TYPE = "CALL searchAvailableRoomsByDatesAndNumberOfPeopleAndRoomType (?,?,?,?)";
    private static final String GET_ROOM_BY_ID = "SELECT hotel_room.id, room_info.id, capacity, price, room_info.description, image_url, room_type.id, name,room_type.description FROM hotel_room JOIN room_info on room_info_id=room_info.id JOIN room_type ON type_id=room_type.id WHERE hotel_room.id=?";
    private static final String CHECK_ROOM_AVAILABILITY_BY_DATES = "SELECT DISTINCT hotel_room_id FROM booking_has_hotel_room JOIN booking on booking_id=id" +
            " WHERE (checkOut_date BETWEEN (?+INTERVAL 1 DAY) AND ? OR checkIn_date BETWEEN ? AND (?-INTERVAL 1 DAY))AND booking.status_id NOT IN (2,4) AND hotel_room_id=?";
    private static final String INSERT_ROOM_TYPE = "INSERT INTO room_type(name,description)VALUES(?,?)";
    private static final String INSERT_ROOM_INFO = "INSERT INTO room_info (type_id,capacity,price,description,image_url) VALUES(?,?,?,?,?)";
    private static final String INSERT_ROOM = "INSERT INTO hotel_room (id, room_info_id) VALUES(?,?)";
    private static  final String UPDATE_ROOM_TYPE = "UPDATE room_type SET name = ?, description = ? WHERE id = ?";
    public static final String UPDATE_ROOM_INFO = "UPDATE room_info SET type_id = ?, capacity = ?, price = ?, description = ?, image_url = ? WHERE id = ?";
    public static final String UPDATE_ROOM = "UPDATE hotel_room SET room_info_id = ? WHERE id = ?";
    private HotelRoom extractRoom(ResultSet resultSet) throws SQLException {
        HotelRoom room = new HotelRoom();
        room.setId(resultSet.getLong(1));
        RoomInfo info = new RoomInfo();
        info.setId(resultSet.getLong(2));
        info.setCapacity(resultSet.getInt(3));
        info.setPrice(resultSet.getDouble(4));
        info.setDescription(resultSet.getString(5));
        info.setImageUrl(resultSet.getString(6));
        RoomType type = new RoomType();
        type.setId(resultSet.getLong(7));
        type.setName(resultSet.getString(8));
        type.setDescription(resultSet.getString(9));
        info.setType(type);
        room.setInfo(info);
        return room;
    }

    @Override
    public List<HotelRoom> getAllRooms() throws DataNotFoundException {
        List<HotelRoom> hotelRoomList = new ArrayList<>();
        try(Connection connection = dataSource.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(GET_ALL_ROOMS)) {
            while (resultSet.next()) {
                HotelRoom room = extractRoom(resultSet);
                hotelRoomList.add(room);
            }
            if(hotelRoomList.size()==0) throw new DataNotFoundException();
        }
        catch (SQLException ex){
            log.error(ex.getMessage());
            throw new DataNotFoundException();
        }
        return hotelRoomList;
    }

    @Override
    public List<HotelRoom> getAllRoomsThanGroupByInfo() throws DataNotFoundException {
        List<HotelRoom> hotelRoomList = new ArrayList<>();
        try(Connection connection = dataSource.getConnection();
                Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(GET_ALL_ROOMS_GROUP_BY_ROOM_INFO)) {
            while (resultSet.next()) {
                HotelRoom room = extractRoom(resultSet);
                hotelRoomList.add(room);
            }
            if(hotelRoomList.size()==0) throw new DataNotFoundException();
        }
        catch (SQLException ex){
            log.error(ex.getMessage());
            throw new DataNotFoundException();
        }
        return hotelRoomList;
    }

    @Override
    public List<HotelRoom> getAllRoomsByTypeAndCapacity(String roomType, int capacity) throws DataNotFoundException {
        List<HotelRoom> hotelRoomList = new ArrayList<>();
        try(Connection connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareCall(GET_ALL_ROOMS_BY_TYPE_AND_CAPACITY_GROUP_BY_ROOM_INFO)) {
            preparedStatement.setString(1, roomType);
            preparedStatement.setInt(2, capacity);
            try(ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    HotelRoom room = extractRoom(resultSet);
                    hotelRoomList.add(room);
                }
            }
            if(hotelRoomList.size()==0) throw new DataNotFoundException();
        }
        catch (SQLException ex){
            log.error(ex.getMessage());
            throw new DataNotFoundException();
        }
        return hotelRoomList;
    }

    @Override
    public List<HotelRoom> getAllAvailableRoomsByDates(Date checkIn, Date checkOut) throws DataNotFoundException {
        List<HotelRoom> hotelRoomList = new ArrayList<>();
        try(Connection connection = dataSource.getConnection();
                CallableStatement callableStatement = connection.prepareCall(CALL_PROCEDURE_GET_ALL_AVAILABLE_ROOMS_BY_DATES)) {
            callableStatement.setString(1,checkIn.toString());
            callableStatement.setString(2, checkOut.toString());
            try(ResultSet resultSet = callableStatement.executeQuery()) {
                while (resultSet.next()) {
                    HotelRoom room = extractRoom(resultSet);
                    hotelRoomList.add(room);
                }
            }
            if(hotelRoomList.size()==0) throw new DataNotFoundException();
        }
        catch (SQLException ex){
            log.error(ex.getMessage());
            throw new DataNotFoundException();
        }
        return hotelRoomList;
    }

    @Override
    public List<HotelRoom> getAllAvailableRoomsByDatesAndNumberOfPeople(Date checkIn, Date checkOut, int numberOfPeople) throws DataNotFoundException {
        List<HotelRoom> hotelRoomList = new ArrayList<>();
        try(Connection connection = dataSource.getConnection();
                CallableStatement callableStatement =
                    connection.prepareCall(CALL_PROCEDURE_GET_ALL_AVAILABLE_ROOMS_BY_DATES_AND_NUMBER_OF_PEOPLE)) {
            callableStatement.setString(1,checkIn.toString());
            callableStatement.setString(2, checkOut.toString());
            callableStatement.setInt(3,numberOfPeople);
            try(ResultSet resultSet = callableStatement.executeQuery()) {
                while (resultSet.next()) {
                    HotelRoom room = extractRoom(resultSet);
                    hotelRoomList.add(room);
                }
            }
            if(hotelRoomList.size()==0)throw new DataNotFoundException();
        }
        catch (SQLException ex){
            log.error(ex.getMessage());
            throw new DataNotFoundException();
        }
        return hotelRoomList;
    }

    @Override
    public List<HotelRoom> getAllAvailableRoomsByDatesAndType(Date checkIn, Date checkOut, String roomType) throws DataNotFoundException {
        List<HotelRoom> hotelRoomList = new ArrayList<>();
        try(Connection connection = dataSource.getConnection();
                CallableStatement callableStatement =
                    connection.prepareCall(CALL_PROCEDURE_GET_ALL_AVAILABLE_ROOMS_BY_DATES_AND_ROOM_TYPE)) {
            callableStatement.setString(1,checkIn.toString());
            callableStatement.setString(2, checkOut.toString());
            callableStatement.setString(3, roomType);
            try(ResultSet resultSet = callableStatement.executeQuery()) {
                while (resultSet.next()) {
                    HotelRoom room = extractRoom(resultSet);
                    hotelRoomList.add(room);
                }
            }
            if(hotelRoomList.size()==0)throw new DataNotFoundException();
        }
        catch (SQLException ex){
            log.error(ex.getMessage());
            throw new DataNotFoundException();
        }
        return hotelRoomList;
    }

    @Override
    public List<HotelRoom> getAllAvailableRoomsByDatesAndNumberOfPeopleAndType(Date checkIn, Date checkOut, int numberOfPeople, String roomType) throws DataNotFoundException {
        List<HotelRoom> hotelRoomList = new ArrayList<>();
        try(Connection connection = dataSource.getConnection();
                CallableStatement callableStatement =
                            connection.prepareCall(CALL_PROCEDURE_GET_ALL_AVAILABLE_ROOMS_BY_DATES_AND_NUMBER_OF_PEOPLE_AND_ROOM_TYPE)) {
            callableStatement.setString(1,checkIn.toString());
            callableStatement.setString(2, checkOut.toString());
            callableStatement.setInt(3,numberOfPeople);
            callableStatement.setString(4,roomType);
            try(ResultSet resultSet = callableStatement.executeQuery()) {
                while (resultSet.next()) {
                    HotelRoom room = extractRoom(resultSet);
                    hotelRoomList.add(room);
                }
            }
            if(hotelRoomList.size()==0) throw new DataNotFoundException();
        }
        catch (SQLException ex){
            log.error(ex.getMessage());
            throw new DataNotFoundException();
        }
        return hotelRoomList;
    }

    @Override
    public HotelRoom getRoomById(long roomId) throws DataNotFoundException {
        HotelRoom room;
        try(Connection connection = dataSource.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(GET_ROOM_BY_ID)) {
            preparedStatement.setLong(1, roomId);
            try(ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    room = extractRoom(resultSet);
                } else {
                    throw new DataNotFoundException();
                }
            }
        } catch (SQLException ex) {
            log.error(ex.getMessage());
            throw new DataNotFoundException();
        }
        return room;
    }

    @Override
    public boolean checkRoomAvailabilityByDates(long roomId, Date checkIn, Date checkOut) throws DataNotFoundException {
        long id = -1;
        try(Connection connection = dataSource.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(CHECK_ROOM_AVAILABILITY_BY_DATES) ) {
            preparedStatement.setString(1, checkIn.toString());
            preparedStatement.setString(3, checkIn.toString());
            preparedStatement.setString(2, checkOut.toString());
            preparedStatement.setString(4, checkOut.toString());
            preparedStatement.setLong(5, roomId);
            try(ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    id = resultSet.getLong(1);
                }
            }
        } catch (SQLException ex) {
            log.error(ex.getMessage());
            throw new DataNotFoundException();
        }
        return !(id == roomId);
    }

    @Override
    public Long insertRoomType(RoomType roomType) {
        try(Connection connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(INSERT_ROOM_TYPE, Statement.RETURN_GENERATED_KEYS)){
            preparedStatement.setString(1,roomType.getName());
            preparedStatement.setString(2, roomType.getDescription());
            preparedStatement.executeUpdate();
            ResultSet key = preparedStatement.getGeneratedKeys();
            if(key.next()) return key.getLong(1);
            else throw new SQLException("Cannot create new room type");
        } catch (SQLException ex){
            log.error(ex.getMessage());
        }
        return (long) -1;
    }

    @Override
    public Long insertRoomInfo(RoomInfo roomInfo) {
        try(Connection connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(INSERT_ROOM_INFO, Statement.RETURN_GENERATED_KEYS)){
            preparedStatement.setLong(1,roomInfo.getType().getId());
            preparedStatement.setLong(2, roomInfo.getCapacity());
            preparedStatement.setDouble(3, roomInfo.getPrice());
            preparedStatement.setString(4, roomInfo.getDescription());
            preparedStatement.setString(5, roomInfo.getImageUrl());
            preparedStatement.executeUpdate();
            ResultSet key = preparedStatement.getGeneratedKeys();
            if(key.next()) return key.getLong(1);
            else throw new SQLException("Cannot create new room info");
        } catch (SQLException ex){
            log.error(ex.getMessage());
        }
        return (long) -1;
    }

    @Override
    public Long insertRoom(HotelRoom room) {
        try(Connection connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(INSERT_ROOM, Statement.RETURN_GENERATED_KEYS)){
            preparedStatement.setLong(1, room.getId());
            preparedStatement.setLong(2, room.getInfo().getId());
            preparedStatement.executeUpdate();
            ResultSet key = preparedStatement.getGeneratedKeys();
            if(key.next()) return key.getLong(1);
            else throw new SQLException("Cannot create new room");
        }catch (SQLException ex){
            log.error(ex.getMessage());
        }
        return (long) -1;
    }

    @Override
    public boolean updateRoomType(RoomType roomType) {
        int affectedRows = -1;
        try(Connection connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_ROOM_TYPE)){
            preparedStatement.setString(1, roomType.getName());
            preparedStatement.setString(2, roomType.getDescription());
            preparedStatement.setLong(3, roomType.getId());
            affectedRows = preparedStatement.executeUpdate();
        }catch (SQLException ex){
            log.error(ex.getMessage());
        }
        return affectedRows == 1;
    }

    @Override
    public boolean updateRoomInfo(RoomInfo roomInfo) {
        int affectedRows = -1;
        try(Connection connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_ROOM_INFO)){
            preparedStatement.setLong(1, roomInfo.getType().getId());
            preparedStatement.setInt(2, roomInfo.getCapacity());
            preparedStatement.setDouble(3, roomInfo.getPrice());
            preparedStatement.setString(4, roomInfo.getDescription());
            preparedStatement.setString(5, roomInfo.getImageUrl());
            preparedStatement.setLong(6, roomInfo.getId());
            affectedRows = preparedStatement.executeUpdate();
        }catch (SQLException ex){
            log.error(ex.getMessage());
        }
        return affectedRows == 1;
    }

    @Override
    public boolean updateRoom(HotelRoom room) {
        int affectedRows = -1;
        try(Connection connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_ROOM)){
            preparedStatement.setLong(1, room.getInfo().getId());
            preparedStatement.setLong(2, room.getId());
            affectedRows = preparedStatement.executeUpdate();
        }catch (SQLException ex){
            log.error(ex.getMessage());
        }
        return affectedRows == 1;
    }
}