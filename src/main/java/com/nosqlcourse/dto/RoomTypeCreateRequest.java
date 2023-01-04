package com.nosqlcourse.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.nosqlcourse.model.RoomType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class RoomTypeCreateRequest {
    String name;
    String description;

    public static RoomType toRoomType(RoomTypeCreateRequest roomTypeCreateRequest){
        RoomType roomType = new RoomType();
        roomType.setName(roomTypeCreateRequest.getName());
        roomType.setDescription(roomTypeCreateRequest.getDescription());
        return roomType;
    }
}
