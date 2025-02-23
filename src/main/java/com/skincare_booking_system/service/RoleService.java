package com.skincare_booking_system.service;


import com.skincare_booking_system.dto.request.RoleRequest;
import com.skincare_booking_system.dto.response.PermissionResponse;
import com.skincare_booking_system.dto.response.RoleResponse;
import com.skincare_booking_system.entity.Role;
import com.skincare_booking_system.mapper.RoleMapper;
import com.skincare_booking_system.repository.PermissionRepository;
import com.skincare_booking_system.repository.RoleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RoleService {
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private PermissionRepository permissionRepository;

    public RoleResponse createRole(RoleRequest request) {
        var role = roleMapper.toRole(request);
        var permissions = permissionRepository.findAllById(request.getPermissions());
        role.setPermissions(new HashSet<>(permissions));
        roleRepository.save(role);
        return roleMapper.toRoleResponse(role);
    }

    public List<RoleResponse> getAllRoles() {
        return roleRepository.findAll().stream().map(roleMapper::toRoleResponse).toList();
    }

    public void deleteRole(String name) {
        roleRepository.deleteById(name);
    }


}
