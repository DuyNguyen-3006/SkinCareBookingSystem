package com.skincare_booking_system.exception;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION("Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    KEY_INVALID( "Key is invalid",HttpStatus.BAD_REQUEST),
    USER_EXISTED( "User already exists",HttpStatus.BAD_REQUEST),
    USERNAME_INVALID("Username must be at least 3 characters",HttpStatus.BAD_REQUEST),
    PASSWORD_INVALID( "Your password must be at least 8 characters",HttpStatus.BAD_REQUEST),
    EMAIL_INVALID( "Your email is not corret",HttpStatus.BAD_REQUEST),
    BLANK_FIELD( "Field cannot be blank",HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED( "User not exists",HttpStatus.NOT_FOUND),
    UNAUTHENTICATION( "Username or password is incorrect",HttpStatus.UNAUTHORIZED),
    PRICE_INVALID("Price must be at least 0",HttpStatus.BAD_REQUEST),
    SERVICE_EXIST( "Service exist",HttpStatus.BAD_REQUEST),
    SERVICE_NOT_FOUND("Service not found",HttpStatus.BAD_REQUEST),
    DESCRIPTION_INVALID("Description is not more than 150",HttpStatus.BAD_REQUEST),
    CATEGORY_INVALID( "Category is not more 50",HttpStatus.BAD_REQUEST),
    ;

    private String message;
    private HttpStatusCode httpStatusCode;

    ErrorCode(String message,HttpStatusCode httpStatusCode) {

        this.message = message;
        this.httpStatusCode=httpStatusCode;
    }
    public HttpStatusCode getHttpStatusCode() {
        return httpStatusCode;
    }

    public String getMessage() {
        return message;
    }
}
