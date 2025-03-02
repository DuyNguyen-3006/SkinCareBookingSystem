package com.skincare_booking_system.mapper;

import com.skincare_booking_system.dto.response.InfoTherapistResponse;
import com.skincare_booking_system.dto.response.TherapistUpdateResponse;
import org.mapstruct.*;

import com.skincare_booking_system.dto.request.TherapistRequest;
import com.skincare_booking_system.dto.request.TherapistUpdateRequest;
import com.skincare_booking_system.dto.response.TherapistResponse;
import com.skincare_booking_system.entities.Therapist;

@Mapper(componentModel = "spring")
public interface TherapistMapper {

    Therapist toTherapist(TherapistRequest therapistRequest);

    TherapistResponse toTherapistResponse(Therapist therapist);

    TherapistUpdateResponse toTherapistUpdateResponse(Therapist therapist);

    InfoTherapistResponse toInfoTherapist(Therapist therapist);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void toUpdateTherapist(@MappingTarget Therapist therapist, TherapistUpdateRequest therapistUpdate);
}
