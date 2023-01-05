package com.nosqlcourse.dao;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mongodb.client.MongoClients;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;
import com.nosqlcourse.config.DataSourceConfiguration;
import com.nosqlcourse.dao.impl.MongoDbAggregationDAOImpl;
import com.nosqlcourse.dao.impl.MongoDbBookingDAOImpl;
import com.nosqlcourse.dao.impl.MongoDbHotelRoomDAOImpl;
import com.nosqlcourse.dao.impl.MongoDbUserDAOImpl;
import org.bson.Document;

import java.lang.reflect.Type;
import java.sql.Date;
import java.time.LocalDate;

public class MongoDbDAOFactory extends DAOFactory{

    public static class LongGsonDeserializer implements JsonDeserializer<Long> {

        @Override
        public Long deserialize(JsonElement jsonElement, Type type,
                                JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            return Long.valueOf(jsonElement.getAsJsonObject().toString().replaceAll("[^\\d]",""));
        }
    }

    public static class DateGsonDeserializer implements JsonDeserializer<Date>{

        @Override
        public Date deserialize(JsonElement jsonElement, Type type,
                                JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            LocalDate date = new Date(Long.parseLong
                    (jsonElement.getAsJsonObject().toString().replaceAll("[^\\d]",""))).toLocalDate().minusDays(1);
            return Date.valueOf(date);
        }
    }

    public static Long getAutoIncrementId(String sequenceId){
        Document query = new Document("_id", sequenceId);
        Document update = new Document("$inc", new Document("sequence_value", 1));
        FindOneAndUpdateOptions options = new FindOneAndUpdateOptions();
        options.returnDocument(ReturnDocument.AFTER);
        options.upsert(true);
        return MongoClients.create(DataSourceConfiguration.getMongoUri()).getDatabase("nosqlcourse")
                .getCollection("counters").findOneAndUpdate(query, update, options).getLong("sequence_value");
    }

    @Override
    public IUserDAO getUserDao() {
        return new MongoDbUserDAOImpl();
    }

    @Override
    public IHotelRoomDAO getHotelRoomDAO() {
        return new MongoDbHotelRoomDAOImpl();
    }

    @Override
    public IBookingDAO getBookingDAO() {
        return new MongoDbBookingDAOImpl();
    }

    public IAggregationDAO getAggregationDAO(){
        return new MongoDbAggregationDAOImpl();
    }
}