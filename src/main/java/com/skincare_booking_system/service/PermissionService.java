package com.skincare_booking_system.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.skincare_booking_system.dto.request.PermissionRequest;
import com.skincare_booking_system.dto.response.PermissionResponse;
import com.skincare_booking_system.entity.Permission;
import com.skincare_booking_system.mapper.PermissionMapper;
import com.skincare_booking_system.repository.PermissionRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PermissionService {
    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private PermissionMapper permissionMapper;

    public PermissionResponse createPermission(PermissionRequest request) {
        Permission permission = permissionMapper.toPermission(request);
        permission = permissionRepository.save(permission);
        return permissionMapper.toPermissionResponse(permission);
    }

    public List<PermissionResponse> getAllPermissions() {
        var permissions = permissionRepository.findAll();
        return permissions.stream().map(permissionMapper::toPermissionResponse).collect(Collectors.toList());
    }

    public void deletePermission(String name) {
        permissionRepository.deleteById(name);
    }
}
