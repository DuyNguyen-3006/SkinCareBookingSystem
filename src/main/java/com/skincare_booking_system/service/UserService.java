package com.skincare_booking_system.service;

import com.skincare_booking_system.dto.request.UserRegisterRequest;
import com.skincare_booking_system.dto.request.UserUpdateRequest;
import com.skincare_booking_system.dto.response.UserResponse;
import com.skincare_booking_system.entity.User;
import com.skincare_booking_system.enums.Roles;
import com.skincare_booking_system.exception.AppException;
import com.skincare_booking_system.exception.ErrorCode;
import com.skincare_booking_system.mapper.UserMapper;
import com.skincare_booking_system.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.management.relation.Role;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@Service
@Slf4j
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    public User registerUser(UserRegisterRequest request) {
        if(userRepository.existsByUsername(request.getUsername())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }

       User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword())); // ma hoa password

        HashSet<String> roles = new HashSet<>();
        roles.add(Roles.USER.name());

        user.setRoles(roles);

        return userRepository.save(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> getAllUsers() {
        log.info("In method getAllUsers");
        return  userRepository.findAll().stream().map(userMapper::toUserResponse).toList();
    }

    @PostAuthorize("returnObject.username == authentication.name")
    public UserResponse getUserByPhoneNumber(String phoneNumber) {
        return userMapper.toUserResponse(userRepository.findByPhone(phoneNumber)
                .orElseThrow(() -> new RuntimeException("User with phone number " + phoneNumber + " not found")));
    }

    public UserResponse updateUser(String userId, UserUpdateRequest request) {
            User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User with id " + id + " not found"));
            userMapper.toUpdateUser(user, request);

        return userMapper.toUserResponse(userRepository.save(user));
    }

    public UserResponse getMyInfo() {
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();

        User user = userRepository.findByUsername(name)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        return userMapper.toUserResponse(user);
    }

    public void deleteUser(String phoneNumber) {
        User user = userRepository.findByPhone(phoneNumber).orElseThrow(() -> new RuntimeException("User with phone number " + phoneNumber + " not found"));
        user.setActive(false);
        userRepository.save(user);
    }
}
