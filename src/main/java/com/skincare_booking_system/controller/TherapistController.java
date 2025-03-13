package com.skincare_booking_system.controller;

import java.util.List;

import com.skincare_booking_system.dto.request.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.skincare_booking_system.dto.response.InfoTherapistResponse;
import com.skincare_booking_system.dto.response.TherapistResponse;
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

    @GetMapping("/{phoneNumber}")
    ApiResponse<TherapistResponse> getTherapist(@PathVariable("phoneNumber") String phone) {
        return ApiResponse.<TherapistResponse>builder()
                .result(therapistService.getTherapistbyPhone(phone))
                .build();
    }

    @PutMapping("/updateTherapist/{phoneNumber}")
    ApiResponse<TherapistUpdateResponse> updateUser(
            @PathVariable String phoneNumber, @RequestBody TherapistUpdateRequest request) {
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

    @PutMapping("/delete/{phoneNumber}")
    public ResponseEntity<String> deleteTherapist(@PathVariable String phoneNumber) {
        therapistService.deleteTherapistbyPhone(phoneNumber);
        return ResponseEntity.ok("Therapist has been deleted");
    }

    @PutMapping("/restore/{phoneNumber}")
    public ResponseEntity<String> restoreTherapist(@PathVariable String phoneNumber) {
        therapistService.restoreTherapistByPhone(phoneNumber);
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
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> resetPassword(
            @PathVariable Long id, @RequestBody ResetPasswordRequest request) {
        therapistService.resetPassword(request, id);
        return ApiResponse.<String>builder().result("Password has been reset").build();
    }
}
