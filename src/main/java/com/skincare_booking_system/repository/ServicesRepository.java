package com.skincare_booking_system.repository;


import com.skincare_booking_system.entities.Services;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
@Repository
public interface ServicesRepository extends JpaRepository<Services, Long> {
    boolean existsByServiceName(String serviceName);
    Optional<Services> findByServiceName(String serviceName);
    List<Services> findByIsActiveTrue();
    List<Services> findByIsActiveFalse();

    List<Services> findServicesByServiceNameIn(Collection<String> serviceNames);
}
