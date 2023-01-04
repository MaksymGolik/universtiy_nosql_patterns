package com.nosqlcourse.model;

import com.google.gson.annotations.SerializedName;

import java.sql.Date;
import java.util.Objects;

public class BookingItem {
    @SerializedName("booking_id")
    private Long bookingId;
    @SerializedName("room_id")
    private Long roomId;
    @SerializedName("check_in")
    private Date checkInDate;
    @SerializedName("check_out")
    private Date checkOutDate;
    @SerializedName("price")
    private double price;

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public Date getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(Date checkInDate) {
        this.checkInDate = checkInDate;
    }

    public Date getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(Date checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookingItem that = (BookingItem) o;
        return Double.compare(that.price, price) == 0 && bookingId.equals(that.bookingId) && roomId.equals(that.roomId) && checkInDate.equals(that.checkInDate) && checkOutDate.equals(that.checkOutDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bookingId, roomId, checkInDate, checkOutDate, price);
    }
}