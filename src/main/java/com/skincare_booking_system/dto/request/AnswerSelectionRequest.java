package com.skincare_booking_system.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class AnswerSelectionRequest {

    private Long userId;
    private List<Long> answerIds;
}
