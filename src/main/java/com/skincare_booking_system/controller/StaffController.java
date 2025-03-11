package com.skincare_booking_system.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.skincare_booking_system.dto.request.ApiResponse;
import com.skincare_booking_system.dto.request.StaffRequest;
import com.skincare_booking_system.dto.request.StaffUpdateRequest;
import com.skincare_booking_system.dto.response.StaffResponse;
import com.skincare_booking_system.service.StaffService;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/staffs")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class StaffController {
    StaffService staffService;

    @PostMapping()
    ApiResponse<StaffResponse> createStaff(@RequestBody StaffRequest request) {
        return ApiResponse.<StaffResponse>builder()
                .result(staffService.createStaff(request))
                .build();
    }

    @GetMapping()
    ApiResponse<List<StaffResponse>> getStaffs() {
        return ApiResponse.<List<StaffResponse>>builder()
                .result(staffService.getAllStaffs())
                .build();
    }

    @GetMapping("/activeStaffs")
    ApiResponse<List<StaffResponse>> getActiveStaffs() {
        return ApiResponse.<List<StaffResponse>>builder()
                .result(staffService.getAllStaffsActive())
                .build();
    }

    @GetMapping("/inactiveStaffs")
    ApiResponse<List<StaffResponse>> getInactiveStaffs() {
        return ApiResponse.<List<StaffResponse>>builder()
                .result(staffService.getAllStaffsInactive())
                .build();
    }

    @GetMapping("/{phoneNumber}")
    ApiResponse<StaffResponse> getStaff(@PathVariable("phoneNumber") String phone) {
        return ApiResponse.<StaffResponse>builder()
                .result(staffService.getStaffsbyPhone(phone))
                .build();
    }

    @GetMapping("/searchByName")
    public ApiResponse<List<StaffResponse>> searchStaffs(@RequestParam String name) {
        return ApiResponse.<List<StaffResponse>>builder()
                .result(staffService.searchStaffsByName(name))
                .build();
    }

    @PutMapping("/delete/{phoneNumber}")
    public ResponseEntity<String> deleteStaff(@PathVariable String phoneNumber) {
        staffService.deleteStaffbyPhone(phoneNumber);
        return ResponseEntity.ok("Staff has been deleted");
    }

    @PutMapping("/restore/{phoneNumber}")
    public ResponseEntity<String> restoreStaff(@PathVariable String phoneNumber) {
        staffService.restoreStaffByPhone(phoneNumber);
        return ResponseEntity.ok("Staff restored successfully");
    }

    @PutMapping("/update/{phone}")
    ApiResponse<StaffResponse> updateStaff(@PathVariable String phone, @RequestBody StaffUpdateRequest request) {
        return ApiResponse.<StaffResponse>builder()
                .result(staffService.updateStaff(phone, request))
                .build();
    }

    @GetMapping("/staffInfo")
    ApiResponse<StaffResponse> getMyInfo() {
        return ApiResponse.<StaffResponse>builder()
                .result(staffService.getMyInfo())
                .build();
    }
}
