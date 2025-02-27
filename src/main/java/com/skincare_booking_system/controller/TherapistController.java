package com.skincare_booking_system.controller;

import com.skincare_booking_system.dto.request.ApiResponse;
import com.skincare_booking_system.dto.request.TherapistRequest;
import com.skincare_booking_system.dto.response.TherapistResponse;
import com.skincare_booking_system.service.TherapistService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("therapists")
public class TherapistController {
    @Autowired
    private TherapistService therapistService;

    @PostMapping()
     ApiResponse<TherapistResponse> createTherapist(@RequestBody TherapistRequest therapistRequest) {
        return ApiResponse.<TherapistResponse>builder()
                .result(therapistService.createTherapist(therapistRequest))
                .build();
    }
}
