package com.skincare_booking_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.skincare_booking_system.entities.InvalidatedToken;
import org.springframework.stereotype.Repository;

@Repository
public interface InvalidatedTokenRepository extends JpaRepository<InvalidatedToken, String> {}
