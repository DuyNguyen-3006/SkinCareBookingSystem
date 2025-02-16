package com.skincare_booking_system.mapper;

import com.skincare_booking_system.dto.request.UserRegisterRequest;
import com.skincare_booking_system.dto.request.UserUpdateRequest;
import com.skincare_booking_system.dto.response.UserResponse;
import com.skincare_booking_system.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(UserRegisterRequest request);
    UserResponse toUserResponse(User user);
    void toUpdateUser(@MappingTarget User user, UserUpdateRequest request);
}
