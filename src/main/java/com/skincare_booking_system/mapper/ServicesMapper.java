package com.skincare_booking_system.mapper;

import com.skincare_booking_system.dto.request.ServicesRequest;
import com.skincare_booking_system.dto.request.ServicesUpdateRequest;
import com.skincare_booking_system.dto.response.ServicesResponse;
import com.skincare_booking_system.entity.Services;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ServicesMapper {
    Services toServices(ServicesRequest serviceRequest);
    void updateServices(@MappingTarget Services service, ServicesUpdateRequest serviceUpdateRequest);
    ServicesResponse toServicesResponse(Services service);
}
