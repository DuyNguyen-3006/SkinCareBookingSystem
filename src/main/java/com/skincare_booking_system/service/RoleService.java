package com.skincare_booking_system.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.skincare_booking_system.dto.request.RoleRequest;
import com.skincare_booking_system.dto.response.RoleResponse;
import com.skincare_booking_system.mapper.RoleMapper;
import com.skincare_booking_system.repository.RoleRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RoleService {
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private RoleMapper roleMapper;



    public RoleResponse createRole(RoleRequest request) {
        var role = roleMapper.toRole(request);
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
