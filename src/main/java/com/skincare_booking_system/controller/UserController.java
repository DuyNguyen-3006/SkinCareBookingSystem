package com.skincare_booking_system.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.skincare_booking_system.dto.request.*;
import com.skincare_booking_system.dto.response.UserResponse;
import com.skincare_booking_system.service.UserService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    UserService userService;

    @PostMapping()
    ApiResponse<UserResponse> registerUser(@RequestBody @Valid UserRegisterRequest request) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.registerUser(request))
                .build();
    }

    @GetMapping()
    ApiResponse<List<UserResponse>> getUserList() {

        var authentication = SecurityContextHolder.getContext().getAuthentication();

        authentication.getAuthorities().forEach(grantedAuthority -> log.info(grantedAuthority.getAuthority()));

        return ApiResponse.<List<UserResponse>>builder()
                .result(userService.getAllUsers())
                .build();
    }

    @GetMapping("/{phoneNumber}")
    ApiResponse<UserResponse> getUser(@PathVariable("phoneNumber") String phone) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.getUserByPhoneNumber(phone))
                .build();
    }

    @GetMapping("/myInfo")
    ApiResponse<UserResponse> getMyInfo() {
        return ApiResponse.<UserResponse>builder()
                .result(userService.getMyInfo())
                .build();
    }

    @PutMapping("/update/{phone}")
    ApiResponse<UserResponse> updateUser(@PathVariable String phone, @RequestBody UserUpdateRequest request) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.updateUser(phone, request))
                .build();
    }

    @DeleteMapping("/{phoneNumber}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    String deleteUser(@PathVariable String phoneNumber) {
        userService.deleteUser(phoneNumber);
        return "User has been deleted";
    }

    @PutMapping("/change-password")
    public ApiResponse<String> changePassword(@RequestBody @Valid ChangePasswordRequest request) {
        userService.changePassword(request);
        return ApiResponse.<String>builder().result("Password has been changed").build();
    }

    @PutMapping("/reset-password/{phoneNumber}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF') or hasRole('THERAPIST')")
    public ApiResponse<String> resetPassword(
            @PathVariable String phoneNumber, @RequestBody ResetPasswordRequest request) {
        userService.resetPassword(request, phoneNumber);
        return ApiResponse.<String>builder().result("Password has been reset").build();
    }
}
