package com.nosqlcourse.model.memento;

import com.nosqlcourse.model.RoomType;
import lombok.Getter;

@Getter
public class RoomInfoMemento {
    private final Long id;
    private final RoomType type;
    private final int capacity;
    private final double price;
    private final String description;
    private final String imageUrl;

    public RoomInfoMemento(Long id, RoomType type, int capacity, double price, String description, String imageUrl) {
        this.id = id;
        this.type = type;
        this.capacity = capacity;
        this.price = price;
        this.description = description;
        this.imageUrl = imageUrl;
    }
}
