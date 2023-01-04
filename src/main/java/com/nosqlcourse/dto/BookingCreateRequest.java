package com.nosqlcourse.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.nosqlcourse.model.Booking;
import com.nosqlcourse.model.BookingItem;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class BookingCreateRequest {
    Long userId;
    String guestSurname;
    String guestName;
    String guestPhoneNumber;
    List<BookingItemCreateRequest> items;

    public static Booking toBooking(BookingCreateRequest bookingCreateRequest){
        return new Booking()
                .guestName(bookingCreateRequest.getGuestName())
                .guestSurname(bookingCreateRequest.getGuestSurname())
                .guestPhoneNumber(bookingCreateRequest.getGuestPhoneNumber())
                .userId(bookingCreateRequest.getUserId());
    }
}
