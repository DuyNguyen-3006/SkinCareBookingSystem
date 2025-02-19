package com.skincare_booking_system.exception;

public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error"),
    KEY_INVALID(1000, "Key is invalid"),
    USER_EXISTED(1001, "User already exists"),
    USERNAME_INVALID(1002, "Username must be at least 3 characters"),
    PASSWORD_INVALID(1003, "Your password must be at least 8 characters"),
    EMAIL_INVALID(1004, "Your email is not corret"),
    BLANK_FIELD(1005, "Field cannot be blank"),
    USER_NOT_EXISTED(1006, "User not exists"),
    UNAUTHENTICATION(1007, "Username or password is incorrect"),
    PRICE_INVALID(1008, "Price must be at least 0"),
    SERVICE_EXIST(1009, "Service exist"),
    SERVICE_NOT_FOUND(1010, "Service not found")
    ;

    private int code;
    private String message;


    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
