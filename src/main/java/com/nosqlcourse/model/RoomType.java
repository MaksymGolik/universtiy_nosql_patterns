package com.nosqlcourse.model;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public class RoomType {
    @SerializedName("_id")
    private Long id;
    @SerializedName("name")
    private String name;
    @SerializedName("description")
    private String description;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RoomType roomType = (RoomType) o;
        return name.equals(roomType.name) && description.equals(roomType.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description);
    }
}