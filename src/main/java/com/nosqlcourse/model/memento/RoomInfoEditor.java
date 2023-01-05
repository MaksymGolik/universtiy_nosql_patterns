package com.nosqlcourse.model.memento;

import com.nosqlcourse.model.RoomInfo;
import com.nosqlcourse.model.RoomType;

import java.util.Deque;
import java.util.LinkedList;

public class RoomInfoEditor {
    private Deque<RoomInfoMemento> history;
    private RoomInfo originator;

    public RoomInfoEditor(RoomInfo originator){
        history = new LinkedList<>();
        this.originator = originator;
        history.add(originator.save());
    }

    public void setType(RoomType roomType){
        originator.setType(roomType);
        history.add(originator.save());
    }

    public void setCapacity(int capacity){
        originator.setCapacity(capacity);
        history.add(originator.save());
    }

    public void setPrice(double price){
        originator.setPrice(price);
        history.add(originator.save());
    }

    public void setDescription(String description){
        originator.setDescription(description);
        history.add(originator.save());
    }

    public void setImageUrl(String imageUrl){
        originator.setImageUrl(imageUrl);
        history.add(originator.save());
    }

    public void undo(){
        originator.restore(history.pop());
    }
}
