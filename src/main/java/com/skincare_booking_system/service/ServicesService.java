package com.skincare_booking_system.service;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.skincare_booking_system.dto.request.ServicesRequest;
import com.skincare_booking_system.dto.request.ServicesUpdateRequest;
import com.skincare_booking_system.dto.response.ServicesResponse;
import com.skincare_booking_system.entities.Package;
import com.skincare_booking_system.entities.Services;
import com.skincare_booking_system.exception.AppException;
import com.skincare_booking_system.exception.ErrorCode;
import com.skincare_booking_system.mapper.ServicesMapper;
import com.skincare_booking_system.repository.PackageRepository;
import com.skincare_booking_system.repository.ServicesRepository;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class ServicesService {
    ServicesRepository servicesRepository;
    PackageRepository packageRepository;
    ServicesMapper servicesMapper;

    public ServicesResponse createServices(ServicesRequest servicesRequest) {
        if (servicesRepository.existsByServiceName(servicesRequest.getServiceName())) {
            throw new AppException(ErrorCode.SERVICE_EXIST);
        }
        Services services = servicesMapper.toServices(servicesRequest);
        return servicesMapper.toServicesResponse(servicesRepository.save(services));
    }

    public List<ServicesResponse> getAllServices() {
        List<Services> services = servicesRepository.findAll();
        if (services.isEmpty()) {
            throw new AppException(ErrorCode.SERVICE_NOT_FOUND);
        }
        return services.stream().map(servicesMapper::toServicesResponse).collect(Collectors.toList());
    }

    public List<ServicesResponse> getAllServicesIsActiveTrue() {
        List<Services> activeServices = servicesRepository.findByIsActiveTrue();
        if (activeServices.isEmpty()) {
            throw new AppException(ErrorCode.SERVICE_NOT_FOUND);
        }
        return activeServices.stream().map(servicesMapper::toServicesResponse).toList();
    }

    public List<ServicesResponse> getAllServicesIsActiveFalse() {

        List<Services> activeServices = servicesRepository.findByIsActiveFalse();
        if (activeServices.isEmpty()) {
            throw new AppException(ErrorCode.SERVICE_NOT_FOUND);
        }
        return activeServices.stream().map(servicesMapper::toServicesResponse).toList();
    }

    public List<ServicesResponse> getServicesByServicesNameCUS(String serviceName) {
        List<Services> services =
                servicesRepository.findServicessByServiceNameContainingIgnoreCaseAndIsActiveTrue(serviceName);
        if (services.isEmpty()) {
            throw new AppException(ErrorCode.SERVICE_NOT_FOUND);
        }
        return services.stream().map(servicesMapper::toServicesResponse).collect(Collectors.toList());
    }

    public List<ServicesResponse> getServicesByServicesName(String serviceName) {
        List<Services> services = servicesRepository.findServicessByServiceNameContainingIgnoreCase(serviceName);
        if (services.isEmpty()) {
            throw new AppException(ErrorCode.SERVICE_NOT_FOUND);
        }
        return services.stream().map(servicesMapper::toServicesResponse).collect(Collectors.toList());
    }

    public ServicesResponse updateServices(String serviceName, ServicesUpdateRequest servicesUpdateRequest) {
        Services services = servicesRepository
                .findByServiceName(serviceName)
                .orElseThrow(() -> new AppException(ErrorCode.SERVICE_NOT_FOUND));
        LocalTime lt = services.getDuration();
        LocalTime lt1 = servicesUpdateRequest.getDuration();
        List<Package> packages = packageRepository.findByServicesIn(List.of(services));
        if (lt != lt1) {
            for (Package p : packages) {
                LocalTime lt3 = p.getDuration();
                LocalTime newTime = lt3.minusHours(lt.getHour())
                        .minusMinutes(lt.getMinute())
                        .minusSeconds(lt.getSecond())
                        .plusHours(lt1.getHour())
                        .plusMinutes(lt1.getMinute())
                        .plusSeconds(lt1.getSecond());
                p.setDuration(newTime);
                packageRepository.save(p);
            }
        }
        servicesMapper.updateServices(services, servicesUpdateRequest);

        return servicesMapper.toServicesResponse(servicesRepository.save(services));
    }

    public String deactivateServices(String serviceName) {
        Services services = servicesRepository
                .findByServiceName(serviceName)
                .orElseThrow(() -> new AppException(ErrorCode.SERVICE_NOT_FOUND));
        services.setIsActive(false);
        servicesRepository.save(services);
        List<Package> packages = packageRepository.findByServicesIn(List.of(services));
        for (Package p : packages) {
            p.setPackageActive(false);
            packageRepository.save(p);
        }
        return "Services deactivated successfully";
    }

    public String activateServices(String serviceName) {
        Services services = servicesRepository
                .findByServiceName(serviceName)
                .orElseThrow(() -> new AppException(ErrorCode.SERVICE_NOT_FOUND));
        services.setIsActive(true);
        servicesRepository.save(services);
        return "Service activated successfully";
    }

    public Long countAllServices() {
        return servicesRepository.countAllServices();
    }
}
