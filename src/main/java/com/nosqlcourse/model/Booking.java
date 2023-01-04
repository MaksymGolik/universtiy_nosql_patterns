package com.nosqlcourse.model;

import com.google.gson.annotations.SerializedName;

import java.sql.Timestamp;
import java.util.Objects;

public class Booking {
    @SerializedName("_id")
    private Long id;
    @SerializedName("user_id")
    private Long userId;
    @SerializedName("status")
    private BookingStatus status;
    @SerializedName("guest_surname")
    private String guestSurname;
    @SerializedName("guest_name")
    private String guestName;
    @SerializedName("guest_phone_number")
    private String guestPhoneNumber;
    @SerializedName("price")
    private double price;
    @SerializedName("create_time")
    private Timestamp createTime;
    @SerializedName("last_update_time")
    private Timestamp lastUpdateTime;

    public Long getId() {
        return id;
    }

    public Booking id(Long id) {
        this.id = id;
        return this;
    }

    public Long getUserId() {
        return userId;
    }

    public Booking userId(Long userId) {
        this.userId = userId;
        return this;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public Booking status(BookingStatus status) {
        this.status = status;
        return this;
    }

    public String getGuestSurname() {
        return guestSurname;
    }

    public Booking guestSurname(String guestSurname) {
        this.guestSurname = guestSurname;
        return this;
    }

    public String getGuestName() {
        return guestName;
    }

    public Booking guestName(String guestName) {
        this.guestName = guestName;
        return this;
    }

    public String getGuestPhoneNumber() {
        return guestPhoneNumber;
    }

    public Booking guestPhoneNumber(String guestPhoneNumber) {
        this.guestPhoneNumber = guestPhoneNumber;
        return this;
    }

    public double getPrice() {
        return price;
    }

    public Booking price(double price) {
        this.price = price;
        return this;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public Booking createTime(Timestamp createTime) {
        this.createTime = createTime;
        return this;
    }

    public Timestamp getLastUpdateTime() {
        return lastUpdateTime;
    }

    public Booking lastUpdateTime(Timestamp lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Booking booking = (Booking) o;
        return Double.compare(booking.price, price) == 0 && id.equals(booking.id)
                && userId.equals(booking.userId) && status.equals(booking.status)
                && guestSurname.equals(booking.guestSurname) && guestName.equals(booking.guestName)
                && guestPhoneNumber.equals(booking.guestPhoneNumber) && createTime.equals(booking.createTime)
                && lastUpdateTime.equals(booking.lastUpdateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, status, guestSurname, guestName, guestPhoneNumber, price, createTime, lastUpdateTime);
    }
}
