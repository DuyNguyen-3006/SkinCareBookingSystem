package com.skincare_booking_system.mapper;

import com.skincare_booking_system.dto.request.PermissionRequest;
import com.skincare_booking_system.dto.response.PermissionResponse;
import com.skincare_booking_system.entity.Permission;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    Permission toPermission(PermissionRequest request);
    PermissionResponse toPermissionResponse(Permission permission);
}
