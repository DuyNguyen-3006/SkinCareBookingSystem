package com.skincare_booking_system.service;

import java.util.HashSet;
import java.util.List;


import com.skincare_booking_system.dto.request.ChangePasswordRequest;
import com.skincare_booking_system.dto.request.ResetPasswordRequest;
import com.skincare_booking_system.entities.User;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.skincare_booking_system.constant.Roles;
import com.skincare_booking_system.dto.request.TherapistRequest;
import com.skincare_booking_system.dto.request.TherapistUpdateRequest;
import com.skincare_booking_system.dto.response.InfoTherapistResponse;
import com.skincare_booking_system.dto.response.TherapistResponse;
import com.skincare_booking_system.dto.response.TherapistUpdateResponse;
import com.skincare_booking_system.entities.Booking;
import com.skincare_booking_system.entities.Role;
import com.skincare_booking_system.entities.Therapist;
import com.skincare_booking_system.exception.AppException;
import com.skincare_booking_system.exception.ErrorCode;
import com.skincare_booking_system.mapper.TherapistMapper;
import com.skincare_booking_system.repository.BookingRepository;
import com.skincare_booking_system.repository.RoleRepository;
import com.skincare_booking_system.repository.TherapistRepository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;

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
    private RoleRepository roleRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @PreAuthorize("hasRole('ADMIN')")
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
        HashSet<Role> roles = new HashSet<>();
        roleRepository.findById(Roles.THERAPIST.name()).ifPresent(roles::add);
        therapist.setRoles(roles);
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
    public void changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        Therapist the = therapistRepository.findByUsername(username).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

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
