package com.skincare_booking_system.controller;

import com.skincare_booking_system.dto.request.ServicesRequest;
import com.skincare_booking_system.dto.request.ApiResponse;
import com.skincare_booking_system.dto.response.ServicesResponse;
import com.skincare_booking_system.entity.Services;
import com.skincare_booking_system.service.ServicesService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/services")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class ServicesController {
    ServicesService servicesService;

    @PostMapping
    ApiResponse<Services> createRequest(@RequestBody @Valid ServicesRequest request) {
        ApiResponse<Services> response = new ApiResponse<>();
        response.setResult(servicesService.createServices(request));
        return response;
    }

    @GetMapping
    List<Services> getAllUsers() {
        return servicesService.getAllServices();
    }

    @GetMapping("/{serviceName}")
    ServicesResponse getServicesByServciesName(@PathVariable String serviceName) {
        return servicesService.getServicesByServicesName(serviceName);
    }

    @PutMapping("/{serviceName}")
    ServicesResponse updateServices(@PathVariable String serviceName,@Valid @RequestBody ServicesRequest servicesRequest) {
        return servicesService.updateServices(serviceName, servicesRequest);
    }

    @DeleteMapping("/{serviceName}")
    void deleteUser(@PathVariable String serviceName) {
        servicesService.deleteServices(serviceName);
    }

}
