package com.skincare_booking_system.controller;

import com.skincare_booking_system.dto.request.ApiResponse;
import com.skincare_booking_system.dto.response.SlotResponse;
import com.skincare_booking_system.entities.Slot;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.skincare_booking_system.service.SlotService;

import java.util.List;

@RestController
@RequestMapping("/slot")
public class SlotController {
    @Autowired
    private SlotService slotService;

    @PostMapping("/create")
    ApiResponse<SlotResponse> createSlot(@Valid @RequestBody Slot slot) {
        return ApiResponse.<SlotResponse>builder()
                .result(slotService.createSlot(slot))
                .build();
    }

    @GetMapping("/getAllSlot")
    ApiResponse<List<Slot>> getAllSlot() {
        return ApiResponse.<List<Slot>>builder()
                .result(slotService.getAllSlot())
                .build();
    }


}
