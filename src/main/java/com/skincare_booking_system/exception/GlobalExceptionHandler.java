package com.skincare_booking_system.exception;

import com.skincare_booking_system.dto.request.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice // cho biet day la noi de create exception
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    ResponseEntity<ApiResponse> handlingRuntimeException(Exception exception) {

        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setMessage(ErrorCode.UNCATEGORIZED_EXCEPTION.getMessage());

        return ResponseEntity.badRequest().body(apiResponse);
    }

    @ExceptionHandler(value = AppException.class)
    ResponseEntity<ApiResponse> handlingAppException(AppException exception) {
        ErrorCode errorCode = exception.getErrorCode();


        ApiResponse apiResponse = new ApiResponse();

        apiResponse.setMessage(errorCode.getMessage());

        return ResponseEntity.status(errorCode.getHttpStatusCode())
                .body(apiResponse);
    }

    @ExceptionHandler(value = AccessDeniedException.class)
    ResponseEntity<ApiResponse> handlingAccessDeniedException(AccessDeniedException exception) {
        ErrorCode errorCode = ErrorCode.UNAUTHORIZED;

        return ResponseEntity.status(errorCode.getHttpStatusCode()).body(
                ApiResponse.builder()
                        .message(errorCode.getMessage())
                        .build()
        );
    }


    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<ApiResponse> handlingValidationException(MethodArgumentNotValidException exception) {

        String enumKey = exception.getFieldError().getDefaultMessage();

        ErrorCode errorCode = ErrorCode.KEY_INVALID;

        try {
            errorCode = ErrorCode.valueOf(enumKey);
        } catch (IllegalArgumentException e) {
            log.error(enumKey + " is not a valid key");
        }
        ApiResponse apiResponse = new ApiResponse();


        apiResponse.setMessage(errorCode.getMessage());

        return ResponseEntity.badRequest().body(apiResponse);
    }
}
