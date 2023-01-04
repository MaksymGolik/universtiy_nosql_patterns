package com.nosqlcourse.dao;

import org.bson.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface IAggregationDAO {
    ArrayList<Document> getUsersGroupByNameWithRoleName (String roleName);
    Map<String, Long> getUsersGroupByNameWithRoleNameWithoutAggregation(String roleName);

    ArrayList<Document> getBookingsGroupByStatusesWithUserId(Long userId);
    Map<String, Double> getBookingsGroupByStatusesWithUserIdWithoutAggregation(Long userId);
    ArrayList<Document> getBookingsGroupByRoomWithStatus(String statusName);
    Map<Long, Long> getBookingsGroupByRoomWithStatusWithoutAggregation(String statusName);
    List<Document> getBookingsGroupByItemsSizeWithPriceGt(double price);
    Map<Integer,Long> getBookingsGroupByItemsSizeWithPriceGtWithoutAggregation(double price);
    List<Document> getBookingsGroupByCheckOutWithStatus(String statusName);
    Map<Date,Long> getBookingsGroupByCheckOutWithStatusWithoutAggregation(String statusName);
}
