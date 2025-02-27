package com.skincare_booking_system.service;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;

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
}
