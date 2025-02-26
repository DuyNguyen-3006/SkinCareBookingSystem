package com.skincare_booking_system.service;

import com.skincare_booking_system.dto.request.ServicesRequest;
import com.skincare_booking_system.dto.request.ServicesUpdateRequest;
import com.skincare_booking_system.dto.response.ServicesResponse;
import com.skincare_booking_system.entity.Services;
import com.skincare_booking_system.exception.AppException;
import com.skincare_booking_system.exception.ErrorCode;
import com.skincare_booking_system.mapper.ServicesMapper;
import com.skincare_booking_system.repository.ServicesRepository;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class ServicesService {
  ServicesRepository servicesRepository;
  ServicesMapper servicesMapper;

    @PreAuthorize("hasRole('ADMIN')")
     public ServicesResponse createServices(ServicesRequest servicesRequest) {
         if(servicesRepository.existsByServiceName(servicesRequest.getServiceName())){
             throw new AppException(ErrorCode.SERVICE_EXIST);
         }
         Services services = servicesMapper.toServices(servicesRequest);
         return servicesMapper.toServicesResponse(servicesRepository.save(services));
     }

//      public List<ServicesResponse> getAllServicesIsActiveTrue() {
//          List<Services> activeServices = servicesRepository.findByIsActiveTrue();
//          return activeServices.stream()
//                  .map(servicesMapper::toServicesResponse)
//                  .toList();
//      }
//    @PreAuthorize("hasRole('ADMIN')")
//    public List<ServicesResponse> getAllServicesIsActiveFalse() {
//        List<Services> activeServices = servicesRepository.findByIsActiveFalse();
//        return activeServices.stream()
//                .map(servicesMapper::toServicesResponse)
//                .toList();
//    }


      public ServicesResponse getServicesByServicesName(String serviceName) {
          return servicesMapper.toServicesResponse(servicesRepository.findByServiceName(serviceName).orElseThrow(() -> new AppException(ErrorCode.SERVICE_NOT_FOUND)));
      }
    @PreAuthorize("hasRole('ADMIN')")
      public ServicesResponse updateServices(String serviceName, ServicesUpdateRequest servicesUpdateRequest) {
          Services services = servicesRepository.findByServiceName(serviceName).orElseThrow(() -> new AppException(ErrorCode.SERVICE_NOT_FOUND));
          servicesMapper.updateServices(services, servicesUpdateRequest);
          return servicesMapper.toServicesResponse(servicesRepository.save(services));
      }
    @PreAuthorize("hasRole('ADMIN')")
      public String deactivateServices(String serviceName) {
          Services services = servicesRepository.findByServiceName(serviceName).orElseThrow(() -> new AppException(ErrorCode.SERVICE_NOT_FOUND));
          services.setIsActive(false);
        servicesRepository.save(services);
          return "Services deactivated successfully";
      }
    @PreAuthorize("hasRole('ADMIN')")
    public String activateServices(String serviceName) {
        Services services = servicesRepository.findByServiceName(serviceName)
                .orElseThrow(() -> new AppException(ErrorCode.SERVICE_NOT_FOUND));
        services.setIsActive(true);
        servicesRepository.save(services);
        return "Service activated successfully";
    }

}
