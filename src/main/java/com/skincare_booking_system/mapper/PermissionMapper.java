package com.skincare_booking_system.mapper;

import org.mapstruct.Mapper;

import com.skincare_booking_system.dto.request.PermissionRequest;
import com.skincare_booking_system.dto.response.PermissionResponse;
import com.skincare_booking_system.entity.Permission;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    Permission toPermission(PermissionRequest request);

    PermissionResponse toPermissionResponse(Permission permission);
}
