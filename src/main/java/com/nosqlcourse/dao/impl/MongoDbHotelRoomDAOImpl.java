package com.nosqlcourse.dao.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import com.nosqlcourse.config.DataSourceConfiguration;
import com.nosqlcourse.dao.IHotelRoomDAO;
import com.nosqlcourse.dao.MongoDbDAOFactory;
import com.nosqlcourse.exception.DataNotFoundException;
import com.nosqlcourse.model.*;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MongoDbHotelRoomDAOImpl implements IHotelRoomDAO {

    private String uri = DataSourceConfiguration.getMongoUri();
    Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new MongoDbDAOFactory.DateGsonDeserializer())
            .registerTypeAdapter(Long.class, new MongoDbDAOFactory.LongGsonDeserializer()).create();

    @Override
    public List<HotelRoom> getAllRooms() throws DataNotFoundException {
        List<HotelRoom> rooms = new ArrayList<>();
        try(MongoClient mongoClient = MongoClients.create(uri)){
            MongoDatabase mongoDatabase = mongoClient.getDatabase("nosqlcourse");
            for (Document document : mongoDatabase.getCollection("rooms").find()) {
                rooms.add(gson.fromJson(document.toJson(),HotelRoom.class));
            }
        }
        return rooms;
    }

    @Override
    public List<HotelRoom> getAllRoomsThanGroupByInfo() throws DataNotFoundException {
        List<HotelRoom> rooms = new ArrayList<>();
        try(MongoClient mongoClient = MongoClients.create(uri)){
            MongoDatabase mongoDatabase = mongoClient.getDatabase("nosqlcourse");
            for (Document document : mongoDatabase.getCollection("rooms").find()) {
                rooms.add(gson.fromJson(document.toJson(),HotelRoom.class));
            }
        }
        Map<Object, List<HotelRoom>> map = rooms.stream().collect(Collectors.groupingBy(HotelRoom::getInfo));
        rooms.clear();
        map.forEach((key, value) -> rooms.add(value.get(0)));
        return rooms;
    }

    @Override
    public List<HotelRoom> getAllRoomsByTypeAndCapacity(String roomType, int capacity) throws DataNotFoundException {
        List<HotelRoom> rooms = new ArrayList<>();
        Bson filter = Filters.and(Filters.eq("room_info.room_type.name", roomType), Filters.gte("room_info.capacity", capacity));
        try(MongoClient mongoClient = MongoClients.create(uri)){
            MongoDatabase mongoDatabase = mongoClient.getDatabase("nosqlcourse");
            for(Document document : mongoDatabase.getCollection("rooms").find(filter)){
                rooms.add(gson.fromJson(document.toJson(),HotelRoom.class));
            }
        }
        Map<Object, List<HotelRoom>> map = rooms.stream().collect(Collectors.groupingBy(HotelRoom::getInfo));
        rooms.clear();
        map.forEach((key, value) -> rooms.add(value.get(0)));
        return rooms;
    }

    @Override
    public List<HotelRoom> getAllAvailableRoomsByDates(Date checkIn, Date checkOut) throws DataNotFoundException {
        List<HotelRoom> rooms = new ArrayList<>();
        List<Long> occupiedRooms = new ArrayList<>();
        try(MongoClient mongoClient = MongoClients.create(uri)) {
            MongoDatabase mongoDatabase = mongoClient.getDatabase("nosqlcourse");
            for (Document document : mongoDatabase.getCollection("rooms").find()) {
                rooms.add(gson.fromJson(document.toJson(),HotelRoom.class));
            }

            Bson filter = Filters.or(Filters.and(Filters.gt("items.check_in",checkIn),Filters.lt("items.check_in",checkOut)),
                    Filters.and(Filters.gt("items.check_out",checkIn.toLocalDate().plusDays(1)),Filters.lt("items.check_out",checkOut)));
            MongoCursor<Document> bookings = mongoDatabase.getCollection("bookings").find(filter).iterator();
            if(bookings.hasNext()){
                for(Document item : (ArrayList<Document>)bookings.next().get("items")){
                    occupiedRooms.add(gson.fromJson(item.toJson(), BookingItem.class).getRoomId());
                }
            }
        }

        return rooms.stream().filter(hotelRoom -> !occupiedRooms.contains(hotelRoom.getId())).toList();
    }

    @Override
    public List<HotelRoom> getAllAvailableRoomsByDatesAndNumberOfPeople(Date checkIn, Date checkOut, int numberOfPeople) throws DataNotFoundException {
       return getAllAvailableRoomsByDates(checkIn, checkOut).stream()
               .filter(hotelRoom -> hotelRoom.getInfo().getCapacity()>=numberOfPeople).toList();
    }

    @Override
    public List<HotelRoom> getAllAvailableRoomsByDatesAndType(Date checkIn, Date checkOut, String roomType) throws DataNotFoundException {
        return getAllAvailableRoomsByDates(checkIn,checkOut)
                .stream().filter(hotelRoom -> hotelRoom.getInfo().getType().getName().equals(roomType)).toList();
    }

    @Override
    public List<HotelRoom> getAllAvailableRoomsByDatesAndNumberOfPeopleAndType(Date checkIn, Date checkOut, int numberOfPeople, String roomType) throws DataNotFoundException {
        return getAllAvailableRoomsByDatesAndType(checkIn, checkOut, roomType)
                .stream().filter(hotelRoom -> hotelRoom.getInfo().getCapacity()>=numberOfPeople).toList();
    }

    @Override
    public HotelRoom getRoomById(long roomId) throws DataNotFoundException {
        BasicDBObject query = new BasicDBObject("_id", roomId);
        try(MongoClient mongoClient = MongoClients.create(uri)) {
            MongoDatabase mongoDatabase = mongoClient.getDatabase("nosqlcourse");
            MongoCursor<Document> document = mongoDatabase.getCollection("rooms").find(query).iterator();
            if(document.hasNext()) return gson.fromJson(document.next().toJson(), HotelRoom.class);
            else throw new DataNotFoundException("Room with id " + roomId + " not found.");
        }
    }

    @Override
    public boolean checkRoomAvailabilityByDates(long roomId, Date checkIn, Date checkOut) throws DataNotFoundException {
        Bson filter = Filters.and(Filters.eq("items.room_id",roomId),
                Filters.or(Filters.and(Filters.gt("items.check_in",checkIn),Filters.lt("items.check_in",checkOut)),
                Filters.and(Filters.gt("items.check_out",checkIn.toLocalDate().plusDays(1)),Filters.lt("items.check_out",checkOut))));
        try(MongoClient mongoClient = MongoClients.create(uri)) {
            MongoDatabase mongoDatabase = mongoClient.getDatabase("nosqlcourse");
            return !mongoDatabase.getCollection("bookings").find(filter).iterator().hasNext();
        }
    }

    private static Document toDocument(RoomType roomType){
        Document document = new Document();
        document.put("_id", roomType.getId());
        document.put("name",roomType.getName());
        document.put("description", roomType.getDescription());
        return document;
    }

    @Override
    public Long insertRoomType(RoomType roomType) {
        Long id = MongoDbDAOFactory.getAutoIncrementId("roomtypeid");
        roomType.setId(id);
        Document roomTypeDocument = toDocument(roomType);
        try(MongoClient mongoClient = MongoClients.create(uri)) {
            MongoDatabase mongoDatabase = mongoClient.getDatabase("nosqlcourse");
            mongoDatabase.getCollection("room_types").insertOne(roomTypeDocument);
        }

        return id;
    }

    private static Document toDocument(RoomInfo roomInfo){
        Document document = new Document();
        document.put("_id", roomInfo.getId());
        document.put("room_type",toDocument(roomInfo.getType()));
        document.put("capacity",roomInfo.getCapacity());
        document.put("price",roomInfo.getPrice());
        document.put("description",roomInfo.getDescription());
        document.put("image_url",roomInfo.getImageUrl());
        return document;
    }

    @Override
    public Long insertRoomInfo(RoomInfo roomInfo) {
        Long id = MongoDbDAOFactory.getAutoIncrementId("roominfoid");
        roomInfo.setId(id);
        Document roomInfoDocument = toDocument(roomInfo);
        try(MongoClient mongoClient = MongoClients.create(uri)) {
            MongoDatabase mongoDatabase = mongoClient.getDatabase("nosqlcourse");
            mongoDatabase.getCollection("room_info").insertOne(roomInfoDocument);
        }
        return id;
    }

    private static Document toDocument(HotelRoom hotelRoom){
        Document document = new Document();
        document.put("_id", hotelRoom.getId());
        document.put("room_info", toDocument(hotelRoom.getInfo()));
        return document;
    }

    @Override
    public Long insertRoom(HotelRoom hotelRoom) {
        Document hotelRoomDocument = toDocument(hotelRoom);
        try(MongoClient mongoClient = MongoClients.create(uri)) {
            MongoDatabase mongoDatabase = mongoClient.getDatabase("nosqlcourse");
            mongoDatabase.getCollection("rooms").insertOne(hotelRoomDocument);
        }
        return hotelRoom.getId();
    }

    @Override
    public boolean updateRoomType(RoomType roomType) {
        Bson update = Updates.combine(Updates.set("name", roomType.getName()),
                Updates.set("description", roomType.getDescription()));
        try(MongoClient mongoClient = MongoClients.create(uri)) {
            MongoDatabase mongoDatabase = mongoClient.getDatabase("nosqlcourse");
            UpdateResult updateResult = mongoDatabase.getCollection("room_types")
                    .updateOne(new BasicDBObject("_id", roomType.getId()),update);
            return updateResult.getModifiedCount()>0;
        }
    }

    @Override
    public boolean updateRoomInfo(RoomInfo roomInfo) {
        Bson update = Updates.combine(Updates.set("room_type", toDocument(roomInfo.getType())),
                Updates.set("capacity", roomInfo.getCapacity()), Updates.set("price", roomInfo.getPrice()),
                Updates.set("description", roomInfo.getDescription()), Updates.set("image_url", roomInfo.getImageUrl()));
        try(MongoClient mongoClient = MongoClients.create(uri)) {
            MongoDatabase mongoDatabase = mongoClient.getDatabase("nosqlcourse");
            UpdateResult updateResult = mongoDatabase.getCollection("room_info")
                    .updateOne(new BasicDBObject("_id", roomInfo.getId()),update);
            return updateResult.getModifiedCount()>0;
        }
    }

    @Override
    public boolean updateRoom(HotelRoom hotelRoom) {
        Bson update = Updates.combine(Updates.set("room_info", toDocument(hotelRoom.getInfo())));
        try(MongoClient mongoClient = MongoClients.create(uri)) {
            MongoDatabase mongoDatabase = mongoClient.getDatabase("nosqlcourse");
            UpdateResult updateResult = mongoDatabase.getCollection("rooms")
                    .updateOne(new BasicDBObject("_id", hotelRoom.getId()),update);
            return updateResult.getModifiedCount()>0;
        }
    }
}