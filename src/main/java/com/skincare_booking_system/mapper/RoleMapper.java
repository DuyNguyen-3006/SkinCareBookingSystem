package com.skincare_booking_system.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.skincare_booking_system.dto.request.RoleRequest;
import com.skincare_booking_system.dto.response.RoleResponse;
import com.skincare_booking_system.entity.Role;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    @Mapping(target = "permissions", ignore = true)
    Role toRole(RoleRequest request);

    RoleResponse toRoleResponse(Role role);
}
