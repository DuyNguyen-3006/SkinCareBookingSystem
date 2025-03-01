package com.skincare_booking_system.service;

import java.util.HashSet;
import java.util.List;

import com.skincare_booking_system.dto.response.InfoTherapistResponse;
import com.skincare_booking_system.dto.response.UserResponse;
import com.skincare_booking_system.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.skincare_booking_system.constant.Roles;
import com.skincare_booking_system.dto.request.TherapistRequest;
import com.skincare_booking_system.dto.response.TherapistResponse;
import com.skincare_booking_system.entity.Role;
import com.skincare_booking_system.entity.Therapist;
import com.skincare_booking_system.exception.AppException;
import com.skincare_booking_system.exception.ErrorCode;
import com.skincare_booking_system.mapper.TherapistMapper;
import com.skincare_booking_system.repository.RoleRepository;
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
    private RoleRepository roleRepository;

    @PreAuthorize("hasRole('ADMIN')")
    public TherapistResponse createTherapist(TherapistRequest request) {
        if (therapistRepository.existsByUsername(request.getUsername())) {
            throw new AppException(ErrorCode.USER_EXISTED);
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

        Therapist therapist = therapistRepository.findByUsername(name).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        return therapistMapper.toInfoTherapist(therapist);
    }
}
