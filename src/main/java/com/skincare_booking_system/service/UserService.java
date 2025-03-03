package com.skincare_booking_system.service;

import java.util.HashSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.skincare_booking_system.constant.Roles;
import com.skincare_booking_system.dto.request.ChangePasswordRequest;
import com.skincare_booking_system.dto.request.ResetPasswordRequest;
import com.skincare_booking_system.dto.request.UserRegisterRequest;
import com.skincare_booking_system.dto.request.UserUpdateRequest;
import com.skincare_booking_system.dto.response.UserResponse;
import com.skincare_booking_system.entities.Role;
import com.skincare_booking_system.entities.User;
import com.skincare_booking_system.exception.AppException;
import com.skincare_booking_system.exception.ErrorCode;
import com.skincare_booking_system.mapper.UserMapper;
import com.skincare_booking_system.repository.RoleRepository;
import com.skincare_booking_system.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private EmailService emailService;

    public UserResponse registerUser(UserRegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new AppException(ErrorCode.USERNAME_EXISTED);
        }
        if (userRepository.existsByPhone(request.getPhone())) {
            throw new AppException(ErrorCode.PHONENUMBER_EXISTED);
        }

        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        HashSet<Role> roles = new HashSet<>();
        roleRepository.findById(Roles.CUSTOMER.toString()).ifPresent(roles::add);
        user.setRoles(roles);
        user.setStatus(true);

        emailService.sendWelcomeEmail(user.getEmail());

        return userMapper.toUserResponse(userRepository.save(user));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream().map(userMapper::toUserResponse).toList();
    }

    @PostAuthorize("returnObject.username == authentication.name")
    public UserResponse getUserByPhoneNumber(String phoneNumber) {
        return userMapper.toUserResponse(userRepository
                .findByPhone(phoneNumber)
                .orElseThrow(() -> new RuntimeException("User with phone number " + phoneNumber + " not found")));
    }

    public UserResponse updateUser(String phone, UserUpdateRequest request) {
        User user = userRepository.findByPhone(phone).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        userMapper.toUpdateUser(user, request);

        return userMapper.toUserResponse(userRepository.save(user));
    }

    public UserResponse getMyInfo() {
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();

        User user = userRepository.findByUsername(name).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        return userMapper.toUserResponse(user);
    }

    public void deleteUser(String phoneNumber) {
        User user = userRepository
                .findByPhone(phoneNumber)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User with phone number " + phoneNumber + " not found"));
        user.setStatus(false);
        userRepository.save(user);
    }

    public void changePassword(ChangePasswordRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User user =
                userRepository.findByUsername(username).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new AppException(ErrorCode.PASSWORD_WRONG);
        }

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new AppException(ErrorCode.PASSWORD_NOT_MATCH);
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    public void resetPassword(ResetPasswordRequest request, String phoneNumber) {
        User user =
                userRepository.findByPhone(phoneNumber).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new AppException(ErrorCode.PASSWORD_NOT_MATCH);
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }
}
