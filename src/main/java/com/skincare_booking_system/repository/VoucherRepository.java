package com.skincare_booking_system.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.skincare_booking_system.entities.Voucher;

@Repository
public interface VoucherRepository extends JpaRepository<Voucher, String> {

    boolean existsByVoucherCode(String voucherCode);

    boolean existsByVoucherName(String voucherName);

    List<Voucher> findByQuantity(int quantity);

    Optional<Voucher> findByVoucherCode(String voucherCode);

    List<Voucher> findByIsActiveTrue();

    List<Voucher> findByIsActiveFalse();

    List<Voucher> findByExpiryDateBefore(LocalDate localDate);

    Voucher findVoucherByVoucherId(String id);
}
