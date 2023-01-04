package com.nosqlcourse.model;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public class RoomInfo {
    @SerializedName("_id")

    private Long id;
    @SerializedName("room_type")
    private RoomType type;
    @SerializedName("capacity")
    private int capacity;
    @SerializedName("price")
    private double price;
    @SerializedName("description")
    private String description;
    @SerializedName("image_url")
    private String imageUrl;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RoomType getType() {
        return type;
    }

    public void setType(RoomType type) {
        this.type = type;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RoomInfo roomInfo = (RoomInfo) o;
        return capacity == roomInfo.capacity && Double.compare(roomInfo.price, price) == 0 && type.equals(roomInfo.type) && description.equals(roomInfo.description) && imageUrl.equals(roomInfo.imageUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, capacity, price, description, imageUrl);
    }
}