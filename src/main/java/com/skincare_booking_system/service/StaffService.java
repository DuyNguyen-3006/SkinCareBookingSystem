package com.skincare_booking_system.service;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import com.skincare_booking_system.constant.Roles;
import com.skincare_booking_system.dto.request.ChangePasswordRequest;
import com.skincare_booking_system.dto.request.ResetPasswordRequest;
import com.skincare_booking_system.dto.request.StaffRequest;
import com.skincare_booking_system.dto.request.StaffUpdateRequest;
import com.skincare_booking_system.dto.response.StaffResponse;
import com.skincare_booking_system.entities.Staff;
import com.skincare_booking_system.exception.AppException;
import com.skincare_booking_system.exception.ErrorCode;
import com.skincare_booking_system.mapper.StaffMapper;
import com.skincare_booking_system.repository.StaffRepository;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class StaffService {
    StaffRepository staffRepository;
    StaffMapper staffMapper;
    PasswordEncoder passwordEncoder;

    @PreAuthorize("hasRole('ADMIN')")
    public StaffResponse createStaff(StaffRequest request) {
        if (staffRepository.existsByUsername(request.getUsername())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        if (staffRepository.existsByPhone(request.getPhone())) {
            throw new AppException(ErrorCode.PHONENUMBER_EXISTED);
        }
        if (staffRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.PHONENUMBER_EXISTED);
        }
        Staff staff = staffMapper.toStaff(request);
        staff.setPassword(passwordEncoder.encode(request.getPassword()));
        staff.setRole(Roles.STAFF);
        staff.setStatus(true);
        return staffMapper.toStaffResponse(staffRepository.save(staff));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<StaffResponse> getAllStaffs() {
        return staffRepository.findByStatusTrue().stream()
                .map(staffMapper::toStaffResponse)
                .toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<StaffResponse> getAllStaffsActive() {
        return staffRepository.findByStatusTrue().stream()
                .map(staffMapper::toStaffResponse)
                .toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<StaffResponse> getAllStaffsInactive() {
        return staffRepository.findByStatusFalse().stream()
                .map(staffMapper::toStaffResponse)
                .toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public StaffResponse getStaffsbyPhone(String phone) {
        return staffMapper.toStaffResponse(
                staffRepository.findByPhone(phone).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<StaffResponse> searchStaffsByName(String name) {
        List<Staff> staff = staffRepository.findByFullnameContainingIgnoreCase(name);

        if (staff.isEmpty()) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }
        return staff.stream().map(staffMapper::toStaffResponse).toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteStaffbyPhone(String phone) {
        Staff staff =
                staffRepository.findByPhone(phone).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        staff.setStatus(false);
        staffRepository.save(staff);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void restoreStaffByPhone(String phone) {
        Staff staff =
                staffRepository.findByPhone(phone).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        staff.setStatus(true);
        staffRepository.save(staff);
    }

    public StaffResponse updateStaff(String phone, StaffUpdateRequest request) {
        Staff staff =
                staffRepository.findByPhone(phone).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        staffMapper.toUpdateStaff(staff, request);

        return staffMapper.toStaffResponse(staffRepository.save(staff));
    }

    public StaffResponse getMyInfo() {
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();

        Staff staff =
                staffRepository.findByUsername(name).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        return staffMapper.toStaffResponse(staff);
    }

    public void changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        Staff sta = staffRepository
                .findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if (!passwordEncoder.matches(request.getOldPassword(), sta.getPassword())) {
            throw new AppException(ErrorCode.PASSWORD_WRONG);
        }

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new AppException(ErrorCode.PASSWORD_NOT_MATCH);
        }

        sta.setPassword(passwordEncoder.encode(request.getNewPassword()));
        staffRepository.save(sta);
    }

    public void resetPassword(ResetPasswordRequest request, String id) {
        Staff staff = staffRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new AppException(ErrorCode.PASSWORD_NOT_MATCH);
        }

        staff.setPassword(passwordEncoder.encode(request.getNewPassword()));
        staffRepository.save(staff);
    }
}
