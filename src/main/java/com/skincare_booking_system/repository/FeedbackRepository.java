package com.skincare_booking_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.skincare_booking_system.entities.Feedback;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    Feedback findFeedbackByBookingBookingId(long id);
}
