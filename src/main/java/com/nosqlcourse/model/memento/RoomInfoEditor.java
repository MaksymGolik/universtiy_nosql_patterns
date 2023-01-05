package com.nosqlcourse.model.memento;

import com.nosqlcourse.model.RoomInfo;
import com.nosqlcourse.model.RoomType;

import java.util.LinkedList;
import java.util.Stack;

public class RoomInfoEditor {
    Stack<RoomInfoMemento> history;
    private RoomInfo originator;

    public RoomInfoEditor(RoomInfo originator){
        history = new Stack<>();
        this.originator = originator;
    }

    public void setType(RoomType roomType){
        history.add(originator.save());
        originator.setType(roomType);
    }

    public void setCapacity(int capacity){
        history.add(originator.save());
        originator.setCapacity(capacity);
    }

    public void setPrice(double price){
        history.add(originator.save());
        originator.setPrice(price);
    }

    public void setDescription(String description){
        history.add(originator.save());
        originator.setDescription(description);
    }

    public void setImageUrl(String imageUrl){
        history.add(originator.save());
        originator.setImageUrl(imageUrl);
    }

    public void undo(){
        originator.restore(history.pop());
    }
}
