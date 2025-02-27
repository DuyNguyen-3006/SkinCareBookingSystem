package com.skincare_booking_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.skincare_booking_system.entity.InvalidatedToken;

public interface InvalidatedTokenRepository extends JpaRepository<InvalidatedToken, String> {}
