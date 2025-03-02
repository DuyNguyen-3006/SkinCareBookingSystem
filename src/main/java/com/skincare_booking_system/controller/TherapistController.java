package com.skincare_booking_system.controller;

import java.util.List;

import com.skincare_booking_system.dto.request.TherapistUpdateRequest;
import com.skincare_booking_system.dto.request.UserUpdateRequest;
import com.skincare_booking_system.dto.response.InfoTherapistResponse;
import com.skincare_booking_system.dto.response.TherapistUpdateResponse;
import com.skincare_booking_system.dto.response.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.skincare_booking_system.dto.request.ApiResponse;
import com.skincare_booking_system.dto.request.TherapistRequest;
import com.skincare_booking_system.dto.response.TherapistResponse;
import com.skincare_booking_system.service.TherapistService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/therapists")
public class TherapistController {
    @Autowired
    private TherapistService therapistService;

    @PostMapping()
    ApiResponse<TherapistResponse> createTherapist(@RequestBody TherapistRequest therapistRequest) {
        return ApiResponse.<TherapistResponse>builder()
                .result(therapistService.createTherapist(therapistRequest))
                .build();
    }

    @GetMapping()
    ApiResponse<List<TherapistResponse>> getTherapists() {
        return ApiResponse.<List<TherapistResponse>>builder()
                .result(therapistService.getAllTherapists())
                .build();
    }

    @GetMapping("/activeTherapists")
    ApiResponse<List<TherapistResponse>> getActiveTherapists() {
        return ApiResponse.<List<TherapistResponse>>builder()
                .result(therapistService.getAllTherapistsActive())
                .build();
    }

    @GetMapping("/inactiveTherapists")
    ApiResponse<List<TherapistResponse>> getInactiveTherapists() {
        return ApiResponse.<List<TherapistResponse>>builder()
                .result(therapistService.getAllTherapistsInactive())
                .build();
    }

    @GetMapping("/{phoneNumber}")
    ApiResponse<TherapistResponse> getUser(@PathVariable("phoneNumber") String phone) {
        return ApiResponse.<TherapistResponse>builder()
                .result(therapistService.getTherapistbyPhone(phone))
                .build();
    }

    @PutMapping("/updateTherapist/{phone}")
    ApiResponse<TherapistUpdateResponse> updateUser(@PathVariable String phoneNumber, @RequestBody TherapistUpdateRequest request) {
        return ApiResponse.<TherapistUpdateResponse>builder()
                .result(therapistService.updateTherapist(phoneNumber, request))
                .build();
    }

    @GetMapping("/searchByName")
    public ApiResponse<List<TherapistResponse>> searchTherapists(@RequestParam String name) {
        return ApiResponse.<List<TherapistResponse>>builder()
                .result(therapistService.searchTherapistsByName(name))
                .build();
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteTherapist(@RequestParam String phoneNumber) {
        therapistService.deleteTherapistbyPhone(phoneNumber);
        return ResponseEntity.ok("Therapist has been deleted");
    }

    @PutMapping("/restore")
    public ResponseEntity<String> restoreTherapist(@RequestParam String phoneNumber) {
        therapistService.restoreTherapistByPhone(phoneNumber);
        return ResponseEntity.ok("Therapist restored successfully");
    }

    @GetMapping("/therapistProfile")
    ApiResponse<InfoTherapistResponse> getMyInfo() {
        return ApiResponse.<InfoTherapistResponse>builder()
                .result(therapistService.getMyInfo())
                .build();
    }
}
