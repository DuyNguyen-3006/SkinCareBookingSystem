package com.skincare_booking_system.service;


import com.skincare_booking_system.dto.request.VoucherRequest;
import com.skincare_booking_system.dto.response.VoucherResponse;
import com.skincare_booking_system.entities.Voucher;
import com.skincare_booking_system.exception.AppException;
import com.skincare_booking_system.exception.ErrorCode;
import com.skincare_booking_system.mapper.VoucherMapper;
import com.skincare_booking_system.repository.VoucherRepository;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class VoucherService {

    VoucherRepository voucherRepository;
    VoucherMapper voucherMapper;

    @PreAuthorize("hasRole('ADMIN')")
    public VoucherResponse createVoucher(VoucherRequest request) {
        if (voucherRepository.existsByVoucherCode(request.getVoucherCode())) {
            throw new AppException(ErrorCode.VOUCHER_CODE_EXISTS);
        }
        if (voucherRepository.existsByVoucherName(request.getVoucherName())) {
            throw new AppException(ErrorCode.VOUCHER_NAME_EXISTS);
        }
        if (request.getExpiryDate().isBefore(LocalDate.now())) {
            throw new AppException(ErrorCode.VOUCHER_EXPIRY_DATE_INVALID);
        }
        if (request.getQuantity() <= 0) {
            throw new AppException(ErrorCode.VOUCHER_QUANTITY_INVALID);
        }

        Voucher voucher = voucherMapper.toVoucher(request);
        voucher.setIsActive(true);
        return voucherMapper.toVoucherResponse(voucherRepository.save(voucher));
    }

    public VoucherResponse getVoucherByCode(String voucherCode) {
        Voucher voucher = voucherRepository.findByVoucherCode(voucherCode)
                .orElseThrow(() -> new AppException(ErrorCode.VOUCHER_NOT_FOUND));

        if (!voucher.getIsActive()) {
            throw new AppException(ErrorCode.VOUCHER_NOT_ACTIVE);
        }

        if (voucher.getExpiryDate().isBefore(LocalDate.now())) {
            throw new AppException(ErrorCode.VOUCHER_EXPIRED);
        }

        if (voucher.getQuantity() <= 0) {
            throw new AppException(ErrorCode.VOUCHER_OUT_OF_STOCK);
        }

        return voucherMapper.toVoucherResponse(voucher);
    }

    public List<VoucherResponse> getActiveVouchers() {
        List<Voucher> vouchers = voucherRepository.findByIsActiveTrue();
        if (vouchers.isEmpty()) {
            throw new AppException(ErrorCode.VOUCHER_NOT_FOUND);
        }
        return vouchers.stream()
                .filter(voucher -> voucher.getExpiryDate().isAfter(LocalDate.now()) && voucher.getQuantity() > 0)
                .map(voucherMapper::toVoucherResponse)
                .toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<VoucherResponse> getInactiveVouchers() {
        List<Voucher> vouchers = voucherRepository.findByIsActiveFalse();
        if (vouchers.isEmpty()) {
            throw new AppException(ErrorCode.VOUCHER_NOT_FOUND);
        }
        return vouchers.stream()
                .map(voucherMapper::toVoucherResponse)
                .toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public String deactivateVoucher(String voucherCode) {
        Voucher voucher = voucherRepository.findByVoucherCode(voucherCode)
                .orElseThrow(() -> new AppException(ErrorCode.VOUCHER_NOT_FOUND));

        if (!voucher.getIsActive()) {
            throw new AppException(ErrorCode.VOUCHER_ALREADY_INACTIVE);
        }

        voucher.setIsActive(false);
        voucherRepository.save(voucher);
        return "Voucher deactivated successfully";
    }

    @PreAuthorize("hasRole('ADMIN')")
    public String activateVoucher(String voucherCode) {
        Voucher voucher = voucherRepository.findByVoucherCode(voucherCode)
                .orElseThrow(() -> new AppException(ErrorCode.VOUCHER_NOT_FOUND));

        if (voucher.getIsActive()) {
            throw new AppException(ErrorCode.VOUCHER_ALREADY_ACTIVE);
        }

        if (voucher.getExpiryDate().isBefore(LocalDate.now())) {
            throw new AppException(ErrorCode.VOUCHER_EXPIRED);
        }

        if (voucher.getQuantity() <= 0) {
            throw new AppException(ErrorCode.VOUCHER_QUANTITY_INVALID);
        }

        voucher.setIsActive(true);
        voucherRepository.save(voucher);
        return "Voucher activated successfully";
    }

    public String useVoucher(String voucherCode) {
        Voucher voucher = voucherRepository.findByVoucherCode(voucherCode)
                .orElseThrow(() -> new AppException(ErrorCode.VOUCHER_NOT_FOUND));

        if (!voucher.getIsActive()) {
            throw new AppException(ErrorCode.VOUCHER_NOT_ACTIVE);
        }

        if (voucher.getExpiryDate().isBefore(LocalDate.now())) {
            throw new AppException(ErrorCode.VOUCHER_EXPIRED);
        }

        if (voucher.getQuantity() <= 0) {
            throw new AppException(ErrorCode.VOUCHER_OUT_OF_STOCK);
        }

        voucher.setQuantity(voucher.getQuantity() - 1);
        voucherRepository.save(voucher);
        return "Voucher used successfully";
    }
}
