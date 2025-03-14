package com.skincare_booking_system.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import com.skincare_booking_system.constant.Roles;
import com.skincare_booking_system.dto.request.ChangePasswordRequest;
import com.skincare_booking_system.dto.request.ResetPasswordRequest;
import com.skincare_booking_system.dto.request.TherapistRequest;
import com.skincare_booking_system.dto.request.TherapistUpdateRequest;
import com.skincare_booking_system.dto.response.BookingResponse;
import com.skincare_booking_system.dto.response.InfoTherapistResponse;
import com.skincare_booking_system.dto.response.TherapistResponse;
import com.skincare_booking_system.dto.response.TherapistUpdateResponse;
import com.skincare_booking_system.entities.Booking;
import com.skincare_booking_system.entities.Therapist;
import com.skincare_booking_system.exception.AppException;
import com.skincare_booking_system.exception.ErrorCode;
import com.skincare_booking_system.mapper.TherapistMapper;
import com.skincare_booking_system.repository.BookingRepository;
import com.skincare_booking_system.repository.ServicesRepository;
import com.skincare_booking_system.repository.TherapistRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TherapistService {
    @Autowired
    private TherapistRepository therapistRepository;

    @Autowired
    private TherapistMapper therapistMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ServicesRepository servicesRepository;

    public TherapistResponse createTherapist(TherapistRequest request) {
        if (therapistRepository.existsByUsername(request.getUsername())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        if (therapistRepository.existsByPhone(request.getPhone())) {
            throw new AppException(ErrorCode.PHONENUMBER_EXISTED);
        }
        if (therapistRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        }
        Therapist therapist = therapistMapper.toTherapist(request);
        therapist.setPassword(passwordEncoder.encode(request.getPassword()));
        therapist.setRole(Roles.THERAPIST);
        therapist.setStatus(true);
        return therapistMapper.toTherapistResponse(therapistRepository.save(therapist));
    }

    public List<TherapistResponse> getAllTherapists() {
        return therapistRepository.findByStatusTrue().stream()
                .map(therapistMapper::toTherapistResponse)
                .toList();
    }

    public List<TherapistResponse> getAllTherapistsActive() {
        return therapistRepository.findByStatusTrue().stream()
                .map(therapistMapper::toTherapistResponse)
                .toList();
    }

    public List<TherapistResponse> getAllTherapistsInactive() {
        return therapistRepository.findByStatusFalse().stream()
                .map(therapistMapper::toTherapistResponse)
                .toList();
    }

    public TherapistResponse getTherapistbyPhone(String phone) {
        return therapistMapper.toTherapistResponse(
                therapistRepository.findByPhone(phone).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED)));
    }

    public List<TherapistResponse> searchTherapistsByName(String name) {
        List<Therapist> therapists = therapistRepository.findByFullNameContainingIgnoreCase(name);

        if (therapists.isEmpty()) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }
        return therapists.stream().map(therapistMapper::toTherapistResponse).toList();
    }

    public void deleteTherapistbyPhone(String phone) {
        Therapist therapist =
                therapistRepository.findByPhone(phone).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        therapist.setStatus(false);
        therapistRepository.save(therapist);
    }

    public void restoreTherapistByPhone(String phone) {
        Therapist therapist =
                therapistRepository.findByPhone(phone).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        therapist.setStatus(true);
        therapistRepository.save(therapist);
    }

    public InfoTherapistResponse getMyInfo() {
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();

        Therapist therapist = therapistRepository
                .findByUsername(name)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        return therapistMapper.toInfoTherapist(therapist);
    }

    public TherapistUpdateResponse updateTherapist(String phone, TherapistUpdateRequest request) {
        Therapist therapist =
                therapistRepository.findByPhone(phone).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        therapistMapper.toUpdateTherapist(therapist, request);
        return therapistMapper.toTherapistUpdateResponse(therapistRepository.save(therapist));
    }

    public double calculateAverageFeedback(Long therapistId, String yearAndMonth) {
        String[] parts = yearAndMonth.split("-");
        int year = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]);

        List<Booking> bookings = bookingRepository.findBookingByTherapistIdAndMonthYear(therapistId, month, year);
        // Tính tổng điểm feedback và đếm số lượng feedback
        double totalFeedbackScore = bookings.stream()
                .filter(booking -> booking.getFeedback() != null) // Chỉ tính booking có feedback
                .mapToDouble(booking -> booking.getFeedback().getScore()) // Lấy điểm từ feedback
                .sum();

        long feedbackCount = bookings.stream()
                .filter(booking -> booking.getFeedback() != null) // Chỉ tính booking có feedback
                .count();
        double averageFeedbackScore = feedbackCount > 0 ? totalFeedbackScore / feedbackCount : 0.0;

        log.info(
                "Therapist ID: {}, Total Feedback Score: {}, Average Feedback Score: {}",
                therapistId,
                totalFeedbackScore,
                averageFeedbackScore);

        return averageFeedbackScore;
    }

    public List<BookingResponse> getBookingsForTherapistOnDate(Long therapistId, LocalDate date) {
        List<Booking> bookings = bookingRepository.findAllByTherapistAndDate(therapistId, date);
        // Chuyển đổi lúc trả ra từ Booking sang BookingResponse
        List<BookingResponse> responses = new ArrayList<>();

        for (Booking booking : bookings) {
            // Set<String> serviceNames = serviceRepository.getServiceNameByBooking(booking.getBookingId());
            Set<Long> serviceId = servicesRepository.getServiceIdByBooking(booking.getBookingId());

            BookingResponse bookingResponse = new BookingResponse();
            bookingResponse.setId(booking.getBookingId());
            bookingResponse.setTherapistName(
                    booking.getTherapistSchedule().getTherapist().getFullName());
            bookingResponse.setTime(booking.getSlot().getSlottime());
            bookingResponse.setDate(booking.getBookingDay());
            bookingResponse.setServiceId(serviceId);
            bookingResponse.setStatus(booking.getStatus());
            bookingResponse.setUserId(booking.getUser().getId());
            bookingResponse.setUserName(
                    booking.getUser().getFirstName() + " " + booking.getUser().getLastName());
            if (booking.getVoucher() != null) {
                bookingResponse.setVoucherCode(booking.getVoucher().getVoucherCode());
            }
            responses.add(bookingResponse);
        }
        return responses;
    }

    public void changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        Therapist the = therapistRepository
                .findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if (!passwordEncoder.matches(request.getOldPassword(), the.getPassword())) {
            throw new AppException(ErrorCode.PASSWORD_WRONG);
        }

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new AppException(ErrorCode.PASSWORD_NOT_MATCH);
        }

        the.setPassword(passwordEncoder.encode(request.getNewPassword()));
        therapistRepository.save(the);
    }

    public void resetPassword(ResetPasswordRequest request, Long id) {
        Therapist the =
                therapistRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new AppException(ErrorCode.PASSWORD_NOT_MATCH);
        }

        the.setPassword(passwordEncoder.encode(request.getNewPassword()));
        therapistRepository.save(the);
    }
}
