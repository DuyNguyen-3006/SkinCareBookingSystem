package com.skincare_booking_system.repository;

import com.skincare_booking_system.entities.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {

    List<Answer> findByQuestion_Id(Long questionId);

    @Query("SELECT a FROM Answer a WHERE a.service.serviceId = :serviceId")
    List<Answer> findByServiceId(@Param("serviceId") Long serviceId);
}
