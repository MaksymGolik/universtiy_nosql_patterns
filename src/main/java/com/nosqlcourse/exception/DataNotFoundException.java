package com.nosqlcourse.exception;

public class DataNotFoundException extends RuntimeException {
    public DataNotFoundException() {
        super("Data not found in DB by request");
    }
    public DataNotFoundException(String message) {
        super(message);
    }
}
