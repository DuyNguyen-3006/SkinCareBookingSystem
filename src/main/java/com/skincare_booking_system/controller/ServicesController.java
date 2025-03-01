package com.skincare_booking_system.controller;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;

import com.skincare_booking_system.dto.request.ApiResponse;
import com.skincare_booking_system.dto.request.ServicesRequest;
import com.skincare_booking_system.dto.request.ServicesUpdateRequest;
import com.skincare_booking_system.dto.response.ServicesResponse;
import com.skincare_booking_system.service.ServicesService;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

@RestController
@RequestMapping("/services")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class ServicesController {
    ServicesService servicesService;

    @PostMapping
    ApiResponse<ServicesResponse> createRequest(@RequestBody @Valid ServicesRequest request) {
        ApiResponse<ServicesResponse> response = new ApiResponse<>();
        response.setResult(servicesService.createServices(request));
        return response;
    }

        @GetMapping("/true")
        ApiResponse<List<ServicesResponse>> getAllServicesTrue() {
            return ApiResponse.<List<ServicesResponse>>builder()
                    .result(servicesService.getAllServicesIsActiveTrue()).build();
        }
        @GetMapping("/false")
        ApiResponse<List<ServicesResponse>> getAllServicesFalse() {
            return ApiResponse.<List<ServicesResponse>>builder()
                    .result(servicesService.getAllServicesIsActiveFalse()).build();
        }

    @GetMapping("/{serviceName}")
    ApiResponse<ServicesResponse> getServicesByServciesName(@PathVariable String serviceName) {
        return ApiResponse.<ServicesResponse>builder()
                .result(servicesService.getServicesByServicesName(serviceName))
                .build();
    }

    @PutMapping("/update/{serviceName}")
    ApiResponse<ServicesResponse> updateServices(
            @PathVariable String serviceName, @Valid @RequestBody ServicesUpdateRequest servicesUpdateRequest) {
        return ApiResponse.<ServicesResponse>builder()
                .result(servicesService.updateServices(serviceName, servicesUpdateRequest))
                .build();
    }

    @PutMapping("/deactive/{serviceName}")
    ApiResponse<String> deactivateService(@PathVariable String serviceName) {
        return ApiResponse.<String>builder()
                .result(servicesService.deactivateServices(serviceName))
                .build();
    }

    @PutMapping("/active/{serviceName}")
    ApiResponse<String> activateService(@PathVariable String serviceName) {
        return ApiResponse.<String>builder()
                .result(servicesService.activateServices(serviceName))
                .build();
    }
}
