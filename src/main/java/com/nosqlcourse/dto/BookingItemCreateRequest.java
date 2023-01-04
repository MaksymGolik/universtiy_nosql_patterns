package com.nosqlcourse.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.nosqlcourse.model.BookingItem;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;

@NoArgsConstructor
@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class BookingItemCreateRequest {
    Long hotelRoomId;
    String checkIn;
    String checkOut;

    public static BookingItem toBookingItem(BookingItemCreateRequest bookingItemCreateRequest){
        BookingItem bookingItem = new BookingItem();
        bookingItem.setRoomId(bookingItemCreateRequest.getHotelRoomId());
        bookingItem.setCheckInDate(Date.valueOf(bookingItemCreateRequest.getCheckIn()));
        bookingItem.setCheckOutDate(Date.valueOf(bookingItemCreateRequest.getCheckOut()));
        return bookingItem;
    }
}
