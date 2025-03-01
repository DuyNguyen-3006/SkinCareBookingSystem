package com.skincare_booking_system.mapper;

import org.mapstruct.Mapper;

import com.skincare_booking_system.dto.request.RoleRequest;
import com.skincare_booking_system.dto.response.RoleResponse;
import com.skincare_booking_system.entity.Role;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    Role toRole(RoleRequest request);

    RoleResponse toRoleResponse(Role role);
}
