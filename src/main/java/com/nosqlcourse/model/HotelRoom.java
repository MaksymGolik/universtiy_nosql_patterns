package com.nosqlcourse.model;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public class HotelRoom {
    @SerializedName("_id")
    private Long id;
    @SerializedName("room_info")
    private RoomInfo info;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RoomInfo getInfo() {
        return info;
    }

    public void setInfo(RoomInfo info) {
        this.info = info;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HotelRoom hotelRoom = (HotelRoom) o;
        return id.equals(hotelRoom.id) && info.equals(hotelRoom.info);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, info);
    }
}