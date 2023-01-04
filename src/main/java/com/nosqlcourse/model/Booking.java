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

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public String getGuestSurname() {
        return guestSurname;
    }

    public void setGuestSurname(String guestSurname) {
        this.guestSurname = guestSurname;
    }

    public String getGuestName() {
        return guestName;
    }

    public void setGuestName(String guestName) {
        this.guestName = guestName;
    }

    public String getGuestPhoneNumber() {
        return guestPhoneNumber;
    }

    public void setGuestPhoneNumber(String guestPhoneNumber) {
        this.guestPhoneNumber = guestPhoneNumber;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public Timestamp getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Timestamp lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
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
