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
    UNAUTHENTICATION("Username or password is incorrect", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED("You do not have permission", HttpStatus.FORBIDDEN),

    ;

    private String message;
    private HttpStatusCode httpStatusCode;


    ErrorCode( String message , HttpStatusCode httpStatusCode) {
        this.message = message;
        this.httpStatusCode = httpStatusCode;
    }

}
