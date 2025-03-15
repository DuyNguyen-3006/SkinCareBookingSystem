package com.skincare_booking_system.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.skincare_booking_system.dto.request.*;
import com.skincare_booking_system.dto.response.InfoTherapistResponse;
import com.skincare_booking_system.dto.response.TherapistResponse;
import com.skincare_booking_system.dto.response.TherapistRevenueResponse;
import com.skincare_booking_system.dto.response.TherapistUpdateResponse;
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

    @GetMapping("/{id}")
    ApiResponse<TherapistResponse> getTherapist(@PathVariable("id") String phone) {
        return ApiResponse.<TherapistResponse>builder()
                .result(therapistService.getTherapistbyPhone(phone))
                .build();
    }

    @PutMapping("/updateTherapist/{id}")
    ApiResponse<TherapistUpdateResponse> updateUser(
            @PathVariable String id, @RequestBody TherapistUpdateRequest request) {
        return ApiResponse.<TherapistUpdateResponse>builder()
                .result(therapistService.updateTherapist(id, request))
                .build();
    }

    @GetMapping("/searchByName")
    public ApiResponse<List<TherapistResponse>> searchTherapists(@RequestParam String name) {
        return ApiResponse.<List<TherapistResponse>>builder()
                .result(therapistService.searchTherapistsByName(name))
                .build();
    }

    @PutMapping("/delete/{id}")
    public ResponseEntity<String> deleteTherapist(@PathVariable String id) {
        therapistService.deleteTherapistbyPhone(id);
        return ResponseEntity.ok("Therapist has been deleted");
    }

    @PutMapping("/restore/{id}")
    public ResponseEntity<String> restoreTherapist(@PathVariable String id) {
        therapistService.restoreTherapistByPhone(id);
        return ResponseEntity.ok("Therapist restored successfully");
    }

    @GetMapping("/therapistProfile")
    ApiResponse<InfoTherapistResponse> getMyInfo() {
        return ApiResponse.<InfoTherapistResponse>builder()
                .result(therapistService.getMyInfo())
                .build();
    }

    @PutMapping("/change-password")
    public ApiResponse<String> changePassword(@RequestBody @Valid ChangePasswordRequest request) {
        therapistService.changePassword(request);
        return ApiResponse.<String>builder().result("Password has been changed").build();
    }

    @PutMapping("/reset-password/{id}")
    public ApiResponse<String> resetPassword(@PathVariable Long id, @RequestBody ResetPasswordRequest request) {
        therapistService.resetPassword(request, id);
        return ApiResponse.<String>builder().result("Password has been reset").build();
    }

    @GetMapping("/{therapistId}/revenue/{yearAndMonth}")
    public ApiResponse<TherapistRevenueResponse> getStylistsRevenue(
            @PathVariable long therapistId, @PathVariable String yearAndMonth) {
        TherapistRevenueResponse totalRevenue = therapistService.getTherapistRevenue(therapistId, yearAndMonth);
        return ApiResponse.<TherapistRevenueResponse>builder()
                .result(totalRevenue)
                .build();
    }
}
