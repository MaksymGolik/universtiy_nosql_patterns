package com.nosqlcourse.dao.impl;

import com.google.gson.*;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import com.nosqlcourse.config.DataSourceConfiguration;
import com.nosqlcourse.dao.IBookingDAO;
import com.nosqlcourse.dao.MongoDbDAOFactory;
import com.nosqlcourse.exception.DataNotFoundException;
import com.nosqlcourse.model.Booking;
import com.nosqlcourse.model.BookingItem;
import com.nosqlcourse.model.BookingStatus;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class MongoDbBookingDAOImpl implements IBookingDAO {

    private String uri = DataSourceConfiguration.getMongoUri();
    private final Gson gsonBooking = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss")
            .registerTypeAdapter(Long.class, new MongoDbDAOFactory.LongGsonDeserializer()).create();
    @Override
    public Long createBooking(Booking booking, List<BookingItem> bookingItems) {
        Long id = MongoDbDAOFactory.getAutoIncrementId("bookingid");

        Document document = new Document();
        document.put("_id", id);
        document.put("guest_surname",booking.getGuestSurname());
        document.put("guest_name",booking.getGuestName());
        document.put("guest_phone_number", booking.getGuestPhoneNumber());
        String localDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        document.put("create_time", localDateTime);
        document.put("last_update_time", localDateTime);
        document.put("user_id", booking.getUserId());
        Document status = new Document();
        status.put("_id", booking.getStatus().getId());
        status.put("name", booking.getStatus().getName());
        status.put("description", booking.getStatus().getDescription());
        document.put("status", status);
        ArrayList<Document> items = new ArrayList<>();
        bookingItems.forEach(bookingItem -> {
            Document item = new Document();
            item.put("room_id", bookingItem.getRoomId());
            item.put("check_in", bookingItem.getCheckInDate());
            item.put("check_out", bookingItem.getCheckOutDate());
            items.add(item);
        });
        document.put("items", items);
        try(MongoClient mongoClient = MongoClients.create(uri)) {
            MongoDatabase mongoDatabase = mongoClient.getDatabase("nosqlcourse");
            mongoDatabase.getCollection("bookings").insertOne(document);
        }
        return id;
    }

    @Override
    public BookingStatus getStatus(String statusName) throws DataNotFoundException {
        BasicDBObject query = new BasicDBObject("name", statusName);
        try(MongoClient mongoClient = MongoClients.create(uri)) {
            MongoDatabase mongoDatabase = mongoClient.getDatabase("nosqlcourse");
            MongoCursor<Document> document = mongoDatabase.getCollection("booking_statuses").find(query).iterator();
            if(document.hasNext()) return gsonBooking.fromJson(document.next().toJson(), BookingStatus.class);
            else throw new DataNotFoundException("BookingStatus with name " + statusName + " not found.");
        }
    }


    @Override
    public List<Booking> getBookingsByUserId(long userId) throws DataNotFoundException {
        List<Booking> bookings = new ArrayList<>();
        BasicDBObject query = new BasicDBObject("user_id", userId);
        try(MongoClient mongoClient = MongoClients.create(uri)) {
            MongoDatabase mongoDatabase = mongoClient.getDatabase("nosqlcourse");
            for(Document document : mongoDatabase.getCollection("bookings").find(query)){
                bookings.add(gsonBooking.fromJson(document.toJson(), Booking.class));
            }
        }
        return bookings;
    }

    @Override
    public Booking getBookingById(long bookingId) throws DataNotFoundException {
        BasicDBObject query = new BasicDBObject("_id", bookingId);
        try(MongoClient mongoClient = MongoClients.create(uri)) {
            MongoDatabase mongoDatabase = mongoClient.getDatabase("nosqlcourse");
            MongoCursor<Document> document = mongoDatabase.getCollection("bookings").find(query).iterator();
            if(document.hasNext()) return gsonBooking.fromJson(document.next().toJson(), Booking.class);
            else throw new DataNotFoundException("Booking with id " + bookingId + " not found.");
        }
    }

    @Override
    public List<BookingItem> getBookingItemsByBookingId(long bookingId) throws DataNotFoundException {
        List<BookingItem> bookingItems = new ArrayList<>();
        BasicDBObject query = new BasicDBObject("_id", bookingId);
        try(MongoClient mongoClient = MongoClients.create(uri)) {
            MongoDatabase mongoDatabase = mongoClient.getDatabase("nosqlcourse");
            MongoCursor<Document> document = mongoDatabase.getCollection("bookings").find(query).iterator();
            if(document.hasNext()){
                for(Document item : (ArrayList<Document>)document.next().get("items")){
                    bookingItems.add(gsonBooking.fromJson(item.toJson(), BookingItem.class));
                }
            }
            else throw new DataNotFoundException("Booking with id " + bookingId + " not found.");
        }
        bookingItems.forEach(bookingItem -> bookingItem.setBookingId(bookingId));
        return bookingItems;
    }

    @Override
    public boolean changeBookingStatus(long bookingId, BookingStatus status) {
        Document bookingStatus = new Document();
        bookingStatus.put("_id", status.getId());
        bookingStatus.put("name", status.getName());
        bookingStatus.put("description", status.getDescription());
        Bson update = Updates.combine(Updates.set("status", bookingStatus),
                Updates.set("last_update_time", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
        try(MongoClient mongoClient = MongoClients.create(uri)) {
            MongoDatabase mongoDatabase = mongoClient.getDatabase("nosqlcourse");
            UpdateResult updateResult = mongoDatabase.getCollection("bookings").updateOne(new BasicDBObject("_id", bookingId),update);
            return updateResult.getModifiedCount()>0;
        }
    }

    @Override
    public List<Booking> getBookingsByStatus(String statusName) throws DataNotFoundException {
        List<Booking> bookings = new ArrayList<>();
        BasicDBObject query = new BasicDBObject("status.name", statusName);
        try(MongoClient mongoClient = MongoClients.create(uri)) {
            MongoDatabase mongoDatabase = mongoClient.getDatabase("nosqlcourse");
            for(Document document : mongoDatabase.getCollection("bookings").find(query)){
                bookings.add(gsonBooking.fromJson(document.toJson(), Booking.class));
            }
        }
        return bookings;
    }

    @Override
    public boolean deleteBookingById(long bookingId) {
        try(MongoClient mongoClient = MongoClients.create(uri)) {
            MongoDatabase mongoDatabase = mongoClient.getDatabase("nosqlcourse");
            DeleteResult deleteResult = mongoDatabase.getCollection("bookings").deleteOne(new BasicDBObject("_id",bookingId));
            return deleteResult.getDeletedCount()>0;
        }
    }
}
