package com.skincare_booking_system.controller;

import com.skincare_booking_system.dto.response.FeedbackResponse;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;

import com.skincare_booking_system.dto.request.ApiResponse;
import com.skincare_booking_system.dto.request.FeedbackRequest;
import com.skincare_booking_system.service.FeedbackService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/feedback")
public class FeedbackController {
    private final FeedbackService feedbackService;

    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @PostMapping()
    public ApiResponse creteFeedback(@Valid @RequestBody FeedbackRequest request) {
        return ApiResponse.builder()
                .result(feedbackService.createFeedback(request))
                .build();
    }

    @GetMapping("/{bookingId}")
    public ApiResponse<FeedbackResponse> getFeedback(@PathVariable long bookingId) {
        FeedbackResponse feedbackResponse = feedbackService.getFeedbackByBookingId(bookingId);

        return ApiResponse.<FeedbackResponse>builder()
                .message(feedbackResponse != null ? "Success" : "No feedback yet")
                .result(feedbackResponse)
                .build();

    }
}
