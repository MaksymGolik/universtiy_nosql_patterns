package com.nosqlcourse.dao.impl;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.nosqlcourse.config.DataSourceConfiguration;
import com.nosqlcourse.dao.IAggregationDAO;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.mongodb.client.model.Accumulators.*;
import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.gt;
import static com.mongodb.client.model.Projections.*;
import static com.mongodb.client.model.Sorts.ascending;
import static com.mongodb.client.model.Sorts.descending;

public class MongoDbAggregationDAOImpl implements IAggregationDAO {

    private static String uri = DataSourceConfiguration.getMongoUri();
    private static MongoClient client = MongoClients.create(uri);

    @Override
    public ArrayList<Document> getUsersGroupByNameWithRoleName(String roleName) {
        Bson match = match(eq("role.name",roleName));
        Bson group = group("$name", sum("count",1L),addToSet("names","$name"));
        Bson project = project(fields(excludeId(), include("count", "names")));
        Bson sort = sort(descending("count"));
        return client.getDatabase("nosqlcourse").getCollection("users")
                .aggregate(List.of(match,group,project,sort)).into(new ArrayList<>());
    }

    public Map<String, Long> getUsersGroupByNameWithRoleNameWithoutAggregation(String roleName){
       return client.getDatabase("nosqlcourse").getCollection("users").find()
                .projection(fields(excludeId(),include("name","role.name"))).into(new ArrayList<>())
                .stream().filter(document -> ((Document)document.get("role")).getString("name").equals(roleName))
                .map(document -> document.getString("name"))
               .collect(Collectors.groupingBy(Function.identity(),Collectors.counting()))
                .entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
               .entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
               .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }

    @Override
    public ArrayList<Document> getBookingsGroupByStatusesWithUserId(Long userId) {
        Bson match = match(eq("user_id", userId));
        Bson group = group("$status.name", sum("totalPrice", "$price"), addToSet("status","$status.name"));
        Bson project = project(fields(excludeId(), include("totalPrice","status")));
        Bson sort = sort(ascending("totalPrice"));
        return client.getDatabase("nosqlcourse").getCollection("bookings")
                .aggregate(List.of(match,group,project,sort)).into(new ArrayList<>());
    }

    @Override
    public Map<String, Double> getBookingsGroupByStatusesWithUserIdWithoutAggregation(Long userId) {
        return client.getDatabase("nosqlcourse").getCollection("bookings").find()
                .projection(fields(excludeId(),include("user_id","price","status.name"))).into(new ArrayList<>())
                .stream().filter(document -> document.getLong("user_id").equals(userId))
                .collect(Collectors.groupingBy(document -> ((Document)document.get("status")).getString("name"),
                        Collectors.summingDouble(document->document.getDouble("price"))))
                .entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.naturalOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }

    @Override
    public ArrayList<Document> getBookingsGroupByRoomWithStatus(String statusName) {
        Bson match = match(eq("status.name",statusName));
        Bson group = group("$items.room_id", sum("count",1L),addToSet("room","$items.room_id"));
        Bson project = project(fields(excludeId(), include("count", "room")));
        Bson sort = sort(descending("count"));
        Bson limit = limit(15);
        return client.getDatabase("nosqlcourse").getCollection("bookings")
                .aggregate(List.of(match,group,project,sort,limit)).into(new ArrayList<>());
    }

    @Override
    public Map<Long, Long> getBookingsGroupByRoomWithStatusWithoutAggregation(String statusName) {
    	return  client.getDatabase("nosqlcourse").getCollection("bookings").find()
                .projection(fields(excludeId(),include("status.name","items.room_id"))).into(new ArrayList<>())
                .stream().filter(document -> ((Document)document.get("status")).getString("name").equals(statusName))
                .map(document -> document.getList("items",Document.class))
                .flatMap(Collection::stream).map(document -> document.getLong("room_id"))
                .collect(Collectors.groupingBy(Function.identity(),Collectors.counting()))
                .entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
                .entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).limit(15)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,(e1, e2)->e1, LinkedHashMap::new));
    }

    @Override
    public List<Document> getBookingsGroupByItemsSizeWithPriceGt(double price){
        Bson matches = match(gt("price",price));
        Bson group = group("$size:$items", sum("booking_count",1L));
        Bson project = project(fields(excludeId(), include("booking_count"),computed("items_count","$size")));
        Bson sort = sort(descending("booking_count"));
        return client.getDatabase("nosqlcourse").getCollection("bookings")
                .aggregate(List.of(matches,group,project,sort)).into(new ArrayList<>());
    }

    public Map<Integer,Long> getBookingsGroupByItemsSizeWithPriceGtWithoutAggregation(double price){
        return  client.getDatabase("nosqlcourse").getCollection("bookings").find()
                .projection(fields(excludeId(),include("price","items"))).into(new ArrayList<>())
                .stream().filter(document -> document.getDouble("price")>price)
                .map(document -> document.getList("items",Document.class))
                .collect(Collectors.groupingBy(List::size,Collectors.counting()))
                .entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
                .entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,(e1, e2)->e1, LinkedHashMap::new));
    }

    public List<Document> getBookingsGroupByCheckOutWithStatus(String statusName){
        Bson matches = match(eq("status.name",statusName));
        Bson group = group("$items.check_out", sum("booking_count",1L), addToSet("date","$items.check_out"));
        Bson project = project(fields(excludeId(), include("booking_count","date")));
        Bson sort = sort(descending("date"));
        return client.getDatabase("nosqlcourse").getCollection("bookings")
                .aggregate(List.of(matches,group,project,sort)).into(new ArrayList<>());
    }

    public Map<Date,Long> getBookingsGroupByCheckOutWithStatusWithoutAggregation(String statusName){
        return  client.getDatabase("nosqlcourse").getCollection("bookings").find()
                .projection(fields(excludeId(),include("status.name","items.check_out"))).into(new ArrayList<>())
                .stream().filter(document -> ((Document)document.get("status")).getString("name").equals(statusName))
                .map(document -> document.getList("items",Document.class))
                .flatMap(Collection::stream).map(document ->  document.getDate("check_out"))
                .collect(Collectors.groupingBy(Function.identity(),Collectors.counting()))
                .entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
                .entrySet().stream().sorted(Map.Entry.comparingByKey(Comparator.reverseOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,(e1, e2)->e1, LinkedHashMap::new));
    }
}
