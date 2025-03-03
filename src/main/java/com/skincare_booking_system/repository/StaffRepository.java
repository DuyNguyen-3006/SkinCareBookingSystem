package com.skincare_booking_system.repository;

import com.skincare_booking_system.entities.Staff;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StaffRepository extends JpaRepository<Staff, String> {
    boolean existsByUsername(String username);
    List<Staff> findByStatusTrue();
    List<Staff> findByStatusFalse();

    Optional<Staff> findByPhone(String phone);

    List<Staff> findByFullnameContainingIgnoreCase(String fullName);
    Optional<Staff> findByUsername(String username);
}
