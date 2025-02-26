package com.skincare_booking_system.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import com.skincare_booking_system.constant.Gender;
import com.skincare_booking_system.dto.request.UserRegisterRequest;
import com.skincare_booking_system.dto.request.UserUpdateRequest;
import com.skincare_booking_system.dto.response.UserResponse;
import com.skincare_booking_system.entity.User;
import com.skincare_booking_system.exception.AppException;
import com.skincare_booking_system.exception.ErrorCode;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "gender", source = "gender", qualifiedByName = "convertGender")
    User toUser(UserRegisterRequest request);

    UserResponse toUserResponse(User user);

    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "password", ignore = true)
    void toUpdateUser(@MappingTarget User user, UserUpdateRequest request);

    @Named("convertGender")
    default Gender convertGender(String gender) {
        try {
            return Gender.valueOf(gender.toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new AppException(ErrorCode.GENDER_INVALID);
        }
    }
}
