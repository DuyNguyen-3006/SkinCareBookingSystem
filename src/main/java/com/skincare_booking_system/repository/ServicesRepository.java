package com.skincare_booking_system.repository;

import java.util.List;
import java.util.Optional;

import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.skincare_booking_system.entities.Services;

public interface ServicesRepository extends JpaRepository<Services, Long> {
    boolean existsByServiceName(String serviceName);

    Optional<Services> findByServiceName(String serviceName);

    @Transactional
    void deleteByServiceName(String serviceName);

    List<Services> findByIsActiveFalse();

    List<Services> findByIsActiveTrue();
}
