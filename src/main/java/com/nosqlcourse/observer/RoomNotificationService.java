package com.nosqlcourse.observer;

import com.nosqlcourse.dao.DAOFactory;
import com.nosqlcourse.exception.DataNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RoomNotificationService {
    private final List<Guest> subscribers;
    private final DAOFactory dao;

    public RoomNotificationService(DAOFactory dao) {
        List<Guest> guests;
        this.dao = dao;
        try {
            guests = this.dao.getUserDao().getUsers().stream()
                    .filter(user -> user.getRole().getName().equals(Subscribers.subscribersRole))
                    .map(Guest::new).collect(Collectors.toList());
        } catch (DataNotFoundException dnfe){
            guests = new ArrayList<>();
        }
        subscribers = guests;
    }

    public void subscribe(Guest guest){
        subscribers.add(guest);
    }

    public void unsubscribe(Guest guest){
        subscribers.remove(guest);
    }

    public void notify(String notification){
        for (Guest guest : subscribers){
            guest.update(notification);
        }
    }
}