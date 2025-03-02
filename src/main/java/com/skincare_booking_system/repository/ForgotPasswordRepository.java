package com.skincare_booking_system.repository;

import com.skincare_booking_system.entities.ForgotPassword;
import com.skincare_booking_system.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ForgotPasswordRepository extends JpaRepository<ForgotPassword, Integer> {
    ForgotPassword findForgotPasswordByOtpAndUser(Integer otp, User user);
    ForgotPassword findForgotPasswordByUser(User user);
}
