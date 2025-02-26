package com.skincare_booking_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.skincare_booking_system.entity.Permission;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, String> {}
