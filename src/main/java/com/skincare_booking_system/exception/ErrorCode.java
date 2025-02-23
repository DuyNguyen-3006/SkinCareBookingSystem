package com.skincare_booking_system.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION( "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    KEY_INVALID( "Key is invalid", HttpStatus.BAD_REQUEST),
    USER_EXISTED( "User already exists", HttpStatus.BAD_REQUEST),
    USERNAME_INVALID ("Username must be at least 3 characters", HttpStatus.BAD_REQUEST),
    PASSWORD_INVALID( "Your password must be at least 8 characters", HttpStatus.BAD_REQUEST),
    EMAIL_INVALID("Your email is not corret", HttpStatus.BAD_REQUEST),
    BLANK_FIELD("Field cannot be blank", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED("User not exists", HttpStatus.NOT_FOUND),
    UNAUTHENTICATION("Unthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED("You do not have permission", HttpStatus.FORBIDDEN),
    PHONENUMBER_EXISTED("Your phone number already used", HttpStatus.BAD_REQUEST),
    PHONENUMBER_INVALID("Your phone number is not valid", HttpStatus.BAD_REQUEST),
    GENDER_INVALID("Invalid {gender} selection", HttpStatus.BAD_REQUEST),
    PRICE_INVALID("Price must be at least 0",HttpStatus.BAD_REQUEST),
    SERVICE_EXIST( "Service exist",HttpStatus.BAD_REQUEST),
    SERVICE_NOT_FOUND("Service not found",HttpStatus.BAD_REQUEST),
    DESCRIPTION_INVALID("Description is not more than 150",HttpStatus.BAD_REQUEST),
    CATEGORY_INVALID( "Category is not more 50",HttpStatus.BAD_REQUEST),
    ;

    private String message;
    private HttpStatusCode httpStatusCode;


    ErrorCode( String message , HttpStatusCode httpStatusCode) {
        this.message = message;
        this.httpStatusCode = httpStatusCode;
    }


}
