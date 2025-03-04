package com.skincare_booking_system.controller;


import com.skincare_booking_system.dto.request.ApiResponse;
import com.skincare_booking_system.dto.request.PackageRequest;
import com.skincare_booking_system.dto.request.PackageUpdateRequest;
import com.skincare_booking_system.dto.response.PackageResponse;
import com.skincare_booking_system.service.PackageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/packages")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class PackageController {
    PackageService packageService;
    @PostMapping
    ApiResponse<PackageResponse> createRequest(@RequestBody @Valid PackageRequest request) {
        ApiResponse<PackageResponse> response = new ApiResponse<>();
        response.setResult(packageService.createPackage(request));
        return response;
    }

    @GetMapping("/true")
    ApiResponse<List<PackageResponse>> getAllPackagesTrue() {
        return ApiResponse.<List<PackageResponse>>builder()
                .result(packageService.getPackagesActive()).build();
    }
    @GetMapping("/false")
    ApiResponse<List<PackageResponse>> getAllPackagesFalse() {
        return ApiResponse.<List<PackageResponse>>builder()
                .result(packageService.getPackagesDeactive()).build();
    }

    @GetMapping("/{packageName}")
    ApiResponse<PackageResponse> getPackagesByPackagesName(@PathVariable String packageName) {
        return ApiResponse.<PackageResponse>builder()
                .result(packageService.getPackagesByPackagesName(packageName)).build();
    }

    @PutMapping("/update/{packageName}")
    ApiResponse<PackageResponse> updateServices(@PathVariable String packageName, @Valid @RequestBody PackageUpdateRequest packageUpdateRequest) {
        return ApiResponse.<PackageResponse>builder()
                .result(packageService.updatePackage(packageName,packageUpdateRequest)).build();
    }

    @PutMapping("/deactive/{packageName}")
    ApiResponse<String> deactivatePackage(@PathVariable String packageName) {
        return ApiResponse.<String>builder()
                .result(packageService.deactivePackage(packageName)).build();
    }
    @PutMapping("/active/{packageName}")
    ApiResponse<String> activatePackage(@PathVariable String packageName) {
        return ApiResponse.<String>builder()
                .result(packageService.activePackage(packageName)).build();
    }
}
