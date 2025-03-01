package com.skincare_booking_system.mapper;

import com.skincare_booking_system.dto.response.InfoTherapistResponse;
import org.mapstruct.*;

import com.skincare_booking_system.dto.request.TherapistRequest;
import com.skincare_booking_system.dto.request.TherapistUpdate;
import com.skincare_booking_system.dto.response.TherapistResponse;
import com.skincare_booking_system.entity.Therapist;

@Mapper(componentModel = "spring")
public interface TherapistMapper {

    Therapist toTherapist(TherapistRequest therapistRequest);

    TherapistResponse toTherapistResponse(Therapist therapist);

    InfoTherapistResponse toInfoTherapist(Therapist therapist);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void toUpdateTherapist(@MappingTarget Therapist therapist, TherapistUpdate therapistUpdate);
}
