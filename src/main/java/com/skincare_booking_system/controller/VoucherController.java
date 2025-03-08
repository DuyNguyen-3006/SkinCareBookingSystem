package com.skincare_booking_system.controller;

import com.skincare_booking_system.dto.request.ApiResponse;
import com.skincare_booking_system.dto.request.VoucherRequest;
import com.skincare_booking_system.dto.response.VoucherResponse;
import com.skincare_booking_system.service.VoucherService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vouchers")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class VoucherController {

    VoucherService voucherService;

    @PostMapping
    ApiResponse<VoucherResponse> createVoucher(@RequestBody @Valid VoucherRequest request) {
        return ApiResponse.<VoucherResponse>builder()
                .result(voucherService.createVoucher(request))
                .build();
    }
    @GetMapping
    ApiResponse <List<VoucherResponse>> getAllVouchers() {
        return ApiResponse.<List<VoucherResponse>>builder()
                .result(voucherService.getAllVouchers())
                .build();
    }
    @GetMapping("/{voucherCode}")
    ApiResponse<VoucherResponse> getVoucherByCode(@PathVariable String voucherCode) {
        return ApiResponse.<VoucherResponse>builder()
                .result(voucherService.getVoucherByCode(voucherCode))
                .build();
    }

    @GetMapping("/active")
    ApiResponse<List<VoucherResponse>> getActiveVouchers() {
        return ApiResponse.<List<VoucherResponse>>builder()
                .result(voucherService.getActiveVouchers())
                .build();
    }

    @GetMapping("/deactive")
    ApiResponse<List<VoucherResponse>> getInactiveVouchers() {
        return ApiResponse.<List<VoucherResponse>>builder()
                .result(voucherService.getInactiveVouchers())
                .build();
    }
    @GetMapping("/outofstock")
    ApiResponse<List<VoucherResponse>> getOutOfStockVouchers() {
        return ApiResponse.<List<VoucherResponse>>builder()
                .result(voucherService.getVoucherOutOfStock())
                .build();
    }
    @GetMapping("/expirydate")
    ApiResponse<List<VoucherResponse>> getExpiryDateVouchers() {
        return ApiResponse.<List<VoucherResponse>>builder()
                .result(voucherService.getVoucherExpired())
                .build();
    }

    @PutMapping("/deactive/{voucherCode}")
    ApiResponse<String> deactivateVoucher(@PathVariable String voucherCode) {
        return ApiResponse.<String>builder()
                .result(voucherService.deactivateVoucher(voucherCode))
                .build();
    }

    @PutMapping("/active/{voucherCode}")
    ApiResponse<String> activateVoucher(@PathVariable String voucherCode) {
        return ApiResponse.<String>builder()
                .result(voucherService.activateVoucher(voucherCode))
                .build();
    }

    @PutMapping("/use/{voucherCode}")
    ApiResponse<String> useVoucher(@PathVariable String voucherCode) {
        return ApiResponse.<String>builder()
                .result(voucherService.useVoucher(voucherCode))
                .build();
    }
}
