package com.skincare_booking_system.repository;

import com.skincare_booking_system.entity.Services;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ServicesRepository extends JpaRepository<Services, Long> {
    boolean existsByServiceName(String serviceName);
    Optional<Services> findByServiceName(String serviceName);
    @Transactional
    void deleteByServiceName(String serviceName);
}
