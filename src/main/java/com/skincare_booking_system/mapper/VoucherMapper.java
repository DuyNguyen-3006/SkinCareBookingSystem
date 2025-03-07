package com.skincare_booking_system.mapper;


import com.skincare_booking_system.dto.request.VoucherRequest;
import com.skincare_booking_system.dto.response.VoucherResponse;
import com.skincare_booking_system.entities.Voucher;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface VoucherMapper {

    Voucher toVoucher(VoucherRequest request);
    VoucherResponse toVoucherResponse(Voucher voucher);
}
