package com.skincare_booking_system.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ResultResponse {

    private Long id;
    private Long userId;
    private String answerText;
    private String questionText;
    private String serviceName;
    private String serviceDescription;
    private LocalDateTime createdAt;
}
