package com.skincare_booking_system.controller;

import java.util.List;
import java.util.Set;

import com.skincare_booking_system.dto.request.BookingRequest;
import com.skincare_booking_system.dto.response.BookingResponse;
import com.skincare_booking_system.entities.Booking;
import org.springframework.web.bind.annotation.*;

import com.skincare_booking_system.dto.request.ApiResponse;
import com.skincare_booking_system.dto.request.BookingSlots;
import com.skincare_booking_system.dto.request.BookingTherapist;
import com.skincare_booking_system.dto.response.TherapistForBooking;
import com.skincare_booking_system.entities.Slot;
import com.skincare_booking_system.service.BookingService;
import com.skincare_booking_system.service.ServicesService;
import com.skincare_booking_system.service.TherapistService;

@RestController
@RequestMapping("/booking")
public class BookingController {
    private final TherapistService therapistService;
    private final BookingService bookingService;
    private final ServicesService servicesService;

    public BookingController(
            TherapistService therapistService, BookingService bookingService, ServicesService servicesService) {
        this.therapistService = therapistService;
        this.bookingService = bookingService;
        this.servicesService = servicesService;
    }

    @PostMapping("/therapists")
    public ApiResponse<Set<TherapistForBooking>> getTherapistForBooking(
            @RequestBody BookingTherapist bookingTherapist) {
        return ApiResponse.<Set<TherapistForBooking>>builder()
                .result(bookingService.getTherapistForBooking(bookingTherapist))
                .build();
    }

    @PostMapping("/slots")
    public ApiResponse<List<Slot>> getListSlots(@RequestBody BookingSlots bookingSlots) {
        return ApiResponse.<List<Slot>>builder()
                .result(bookingService.getListSlot(bookingSlots))
                .build();
    }

    @PostMapping("/slots/{bookingId}")
    public ApiResponse<List<Slot>> getSlotsUpdateByCustomer(@RequestBody BookingSlots bookingSlots,
                                                            @PathVariable long bookingId){
       return ApiResponse.<List<Slot>>builder()
               .result(bookingService.getSlotsUpdateByCustomer(bookingSlots, bookingId))
               .build();
    }

    @PostMapping("/createBooking")
    public ApiResponse<Booking> createBooking(@RequestBody BookingRequest request){
        ApiResponse apiResponse = new ApiResponse<>();
        apiResponse.setResult(bookingService.createNewBooking(request));
        apiResponse.setSuccess(true);
        return apiResponse;
    }

    @GetMapping("/{bookingId}")
    public ApiResponse<BookingResponse> getBookingById(@PathVariable long bookingId){
        return ApiResponse.<BookingResponse>builder()
                .result(bookingService.getBookingById(bookingId))
                .build();
    }

    @PutMapping("/update/{bookingId}")
    public ApiResponse<Booking> updateBooking(@PathVariable long bookingId,@RequestBody BookingRequest request){
        ApiResponse apiResponse = new ApiResponse<>();
        apiResponse.setResult(bookingService.updateBooking(bookingId,request));
        apiResponse.setSuccess(true);
        return apiResponse;
    }

    @DeleteMapping("/delete/{bookingId}")
    public ApiResponse deleteBooking(@PathVariable Long bookingId){
        ApiResponse apiResponse = new ApiResponse<>();
        apiResponse.setResult(bookingService.deleteBooking(bookingId));
        apiResponse.setSuccess(true);
        return apiResponse;
    }

//    @GetMapping("/customer/{userId}/pending")
//    public ApiResponse<List<Booking>> getPendingBookings(@PathVariable Long userId){
//       return ApiResponse.<List<Booking>>builder()
//               .result(bookingService.getBookingByStatusPendingByCustomer(userId))
//               .build();
//    }
//
//    @GetMapping("/customer/{userId}/completed")
//    public ApiResponse<List<Booking>> getCompleteBookings(@PathVariable Long userId){
//       return ApiResponse.<List<Booking>>builder()
//               .result(bookingService.getBookingByStatusCompletedByCustomer(userId))
//               .build();
//    }
}
