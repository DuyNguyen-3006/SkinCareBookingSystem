package com.skincare_booking_system.controller;

import java.io.IOException;
import java.time.LocalTime;
import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.skincare_booking_system.dto.request.ApiResponse;
import com.skincare_booking_system.dto.request.ServicesRequest;
import com.skincare_booking_system.dto.request.ServicesUpdateRequest;
import com.skincare_booking_system.dto.response.ServicesResponse;
import com.skincare_booking_system.service.ServicesService;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/services")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class ServicesController {
    ServicesService servicesService;

    @PostMapping
    ApiResponse<ServicesResponse> createRequest(@RequestParam("serviceName") String serviceName,
                                                @RequestParam("description") String description,
                                                @RequestParam("price") Double price,
                                                @RequestParam("isActive") Boolean isActive,
                                                @RequestParam("duration") LocalTime duration,
                                                @RequestParam("image") MultipartFile image) throws IOException {
            ServicesResponse serviceResponse = servicesService.createServices(serviceName, description, price,duration ,isActive, image);
            return ApiResponse.<ServicesResponse>builder()
                    .success(true)
                    .message("Service created successfully")
                    .result(serviceResponse)
                    .build();
    }

    @GetMapping
    ApiResponse<List<ServicesResponse>> getAllServices() {
        return ApiResponse.<List<ServicesResponse>>builder()
                .result(servicesService.getAllServices())
                .build();
    }
    @GetMapping("/{serviceId}")
    ApiResponse<ServicesResponse> SearchServiceById(@PathVariable long serviceId) {
        ApiResponse response = new ApiResponse<>();
        response.setResult(servicesService.searchServiceId(serviceId));
        return response ;
    }

    @GetMapping("/active")
    ApiResponse<List<ServicesResponse>> getAllServicesActive() {
        return ApiResponse.<List<ServicesResponse>>builder()
                .result(servicesService.getAllServicesIsActiveTrue())
                .build();
    }

    @GetMapping("/deactive")
    ApiResponse<List<ServicesResponse>> getAllServicesDeactive() {
        return ApiResponse.<List<ServicesResponse>>builder()
                .result(servicesService.getAllServicesIsActiveFalse())
                .build();
    }

    @GetMapping("/searchByName")
    ApiResponse<List<ServicesResponse>> getServicesByServciesName(@RequestParam String serviceName) {
        return ApiResponse.<List<ServicesResponse>>builder()
                .result(servicesService.getServicesByServicesName(serviceName))
                .build();
    }

    @GetMapping("/searchByNameCUS")
    ApiResponse<List<ServicesResponse>> getServicesByServciesNameCUS(@RequestParam String serviceName) {
        return ApiResponse.<List<ServicesResponse>>builder()
                .result(servicesService.getServicesByServicesNameCUS(serviceName))
                .build();
    }

    @PutMapping("/update/{serviceId}")
    ApiResponse<ServicesResponse> updateServices(
            @PathVariable Long serviceId, @RequestParam("serviceName") String serviceName,
            @RequestParam("description") String description,
            @RequestParam("price") Double price,
            @RequestParam("duration") LocalTime duration,
            @RequestParam("image") MultipartFile image) throws IOException {
        ServicesResponse serviceResponse = servicesService.updateServices(serviceId, serviceName, description, price,duration, image);
        return ApiResponse.<ServicesResponse>builder()
                .success(true)
                .message("Service updated successfully")
                .result(serviceResponse)
                .build();
    }

    @PutMapping("/deactive/{serviceId}")
    ApiResponse<String> deactivateService(@PathVariable long serviceId) {
        return ApiResponse.<String>builder()
                .result(servicesService.deactivateServices(serviceId))
                .build();
    }

    @PutMapping("/active/{serviceId}")
    ApiResponse<String> activateService(@PathVariable long serviceId) {
        return ApiResponse.<String>builder()
                .result(servicesService.activateServices(serviceId))
                .build();
    }
}
