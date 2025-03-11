package com.skincare_booking_system.repository;

import java.time.LocalTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.skincare_booking_system.entities.Services;

@Repository
public interface ServicesRepository extends JpaRepository<Services, Long> {
    boolean existsByServiceName(String serviceName);

    Optional<Services> findByServiceName(String serviceName);

    List<Services> findByIsActiveTrue();

    List<Services> findByIsActiveFalse();

    List<Services> findServicesByServiceNameIn(Collection<String> serviceNames);

    List<Services> findServicessByServiceNameContainingIgnoreCase(String serviceName);

    List<Services> findServicessByServiceNameContainingIgnoreCaseAndIsActiveTrue(String serviceName);

    Services getServiceByServiceId(Long serviceId);

    @Query(
            value = "SELECT sec_to_time(SUM(time_to_sec(s.duration))) FROM services s "
                    + "INNER JOIN booking_detail bd "
                    + "ON s.service_id = bd.service_id "
                    + "WHERE bd.booking_id = ?1",
            nativeQuery = true)
    LocalTime getTotalTime(long bookingId);

    @Query(
            value = "select s.* from services s\n" + "inner join booking_detail bd\n"
                    + "on s.service_id = bd.service_id\n"
                    + "where bd.booking_id = ?1 ",
            nativeQuery = true)
    Set<Services> getServiceForBooking(long bookingId);
}
