package com.skincare_booking_system.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.skincare_booking_system.dto.request.ApiResponse;
import com.skincare_booking_system.dto.request.RoleRequest;
import com.skincare_booking_system.dto.response.RoleResponse;
import com.skincare_booking_system.service.RoleService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/roles")
public class RoleController {
    @Autowired
    private RoleService roleService;

    @PostMapping()
    ApiResponse<RoleResponse> createRoles(@RequestBody RoleRequest request) {
        return ApiResponse.<RoleResponse>builder()
                .result(roleService.createRole(request))
                .build();
    }

    @GetMapping()
    ApiResponse<List<RoleResponse>> getPermissions() {
        return ApiResponse.<List<RoleResponse>>builder()
                .result(roleService.getAllRoles())
                .build();
    }

    @DeleteMapping("/{roleName}")
    String deleteRole(@PathVariable("roleName") String roleName) {
        roleService.deleteRole(roleName);
        return "Role deleted";
    }
}
