package com.skincare_booking_system.controller;

import com.skincare_booking_system.dto.request.ApiResponse;
import com.skincare_booking_system.dto.request.PermissionRequest;
import com.skincare_booking_system.dto.response.PermissionResponse;
import com.skincare_booking_system.service.PermissionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/permissions")
public class PermissionController {
    @Autowired
    private PermissionService permissionService;

    @PostMapping()
    ApiResponse<PermissionResponse> createPermission(@RequestBody PermissionRequest request) {
        return ApiResponse.<PermissionResponse>builder()
                .result(permissionService.createPermission(request))
                .build();
    }

    @GetMapping()
    ApiResponse<List<PermissionResponse>> getPermissions() {
        return ApiResponse.<List<PermissionResponse>>builder()
                .result(permissionService.getAllPermissions())
                .build();
    }

    @DeleteMapping("/{permission}")
    ApiResponse<Void> deletePermission(@PathVariable("permission") String permission) {
        permissionService.deletePermission(permission);
        return ApiResponse.<Void>builder()
                .message("Permission deleted")
                .build();
    }
}
