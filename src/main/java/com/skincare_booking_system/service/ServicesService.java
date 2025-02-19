package com.skincare_booking_system.service;

import com.skincare_booking_system.dto.request.ServicesRequest;
import com.skincare_booking_system.dto.response.ServicesResponse;
import com.skincare_booking_system.entity.Services;
import com.skincare_booking_system.exception.AppException;
import com.skincare_booking_system.exception.ErrorCode;
import com.skincare_booking_system.mapper.ServicesMapper;
import com.skincare_booking_system.repository.ServicesRepository;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class ServicesService {
  ServicesRepository servicesRepository;
  ServicesMapper servicesMapper;


     public Services createServices(ServicesRequest servicesRequest) {
         if(servicesRepository.existsByServiceName(servicesRequest.getServiceName())){
             throw new AppException(ErrorCode.SERVICE_EXIST);
         }
         Services services = servicesMapper.toServices(servicesRequest);
         return servicesRepository.save(services);
     }
      public List<Services> getAllServices() {
          return servicesRepository.findAll();
      }
      public ServicesResponse getServicesByServicesName(String serviceName) {
          return servicesMapper.toServicesResponse(servicesRepository.findByServiceName(serviceName).orElseThrow(() -> new AppException(ErrorCode.SERVICE_NOT_FOUND)));
      }
      public ServicesResponse updateServices(String serviceName, ServicesRequest servicesRequest) {
          Services services = servicesRepository.findByServiceName(serviceName).orElseThrow(() -> new AppException(ErrorCode.SERVICE_NOT_FOUND));
          servicesMapper.updateServices(services, servicesRequest);
          return servicesMapper.toServicesResponse(servicesRepository.save(services));
      }
      public String deleteServices(String serviceName) {
          Services services = servicesRepository.findByServiceName(serviceName).orElseThrow(() -> new AppException(ErrorCode.SERVICE_NOT_FOUND));
          servicesRepository.deleteByServiceName(serviceName);
          return "Services deleted successfully";
      }
}
