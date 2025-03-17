package com.skincare_booking_system.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;

import com.skincare_booking_system.dto.request.VoucherRequest;
import com.skincare_booking_system.dto.response.VoucherResponse;
import com.skincare_booking_system.entities.Voucher;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface VoucherMapper {
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateVoucher(@MappingTarget Voucher v, VoucherRequest voucherRequest);

    Voucher toVoucher(VoucherRequest request);

    VoucherResponse toVoucherResponse(Voucher voucher);
}
