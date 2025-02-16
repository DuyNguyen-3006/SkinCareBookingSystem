package com.skincare_booking_system.controller;

import com.skincare_booking_system.dto.request.ApiResponse;
import com.skincare_booking_system.dto.request.UserRegisterRequest;
import com.skincare_booking_system.dto.request.UserUpdateRequest;
import com.skincare_booking_system.dto.response.UserResponse;
import com.skincare_booking_system.entity.User;
import com.skincare_booking_system.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    UserService userService;

    @PostMapping()
    ApiResponse<User> registerUser(@RequestBody @Valid UserRegisterRequest request) {
        ApiResponse<User> response = new ApiResponse<>();
        response.setResult(userService.registerUser(request));
        return response;
    }

    @GetMapping()
    ApiResponse <List<UserResponse>> getUserList() {

        var authentication = SecurityContextHolder.getContext().getAuthentication();

        log.info("Username: {}", authentication.getName());
        authentication.getAuthorities().forEach(grantedAuthority -> log.info(grantedAuthority.getAuthority()));

        return ApiResponse.<List<UserResponse>>builder()
                .result(userService.getAllUsers())
                .build();
    }

    @GetMapping("/{phoneNumber}")
    ApiResponse <UserResponse> getUser(@PathVariable ("phoneNumber") String phoneNumber) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.getUserByPhoneNumber(phoneNumber))
                .build();
    }

    @GetMapping("/myInfo")
    ApiResponse <UserResponse> getMyInfo() {
        return ApiResponse.<UserResponse>builder()
                .result(userService.getMyInfo())
                .build();
    }

    @PutMapping("/{userId}")
    UserResponse updateUser(@PathVariable String userId, @RequestBody UserUpdateRequest request) {
        return userService.updateUser(userId, request);
    }

    @DeleteMapping("/{phoneNumber}")
    String deleteUser(@PathVariable String phoneNumber) {
        userService.deleteUser(phoneNumber);
        return "User has been deleted";
    }
}
