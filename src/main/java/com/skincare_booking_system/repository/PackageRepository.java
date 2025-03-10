package com.skincare_booking_system.repository;


import com.skincare_booking_system.entities.Services;
import com.skincare_booking_system.entities.Package;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface PackageRepository extends JpaRepository<Package, String> {

    List<Package> findByPackageActiveTrue();

    List<Package> findByPackageActiveFalse();

    Optional<Package> findByPackageName(String packageName);

    List<Package> findByServicesIn(Collection<Services> services);

    boolean existsByPackageName(String packageName);

    List<Package> findPackageByPackageNameContainsIgnoreCase(String packageName);

    List<Package> findPackageByPackageNameContainsIgnoreCaseAndPackageActiveTrue(String packageName);
}
