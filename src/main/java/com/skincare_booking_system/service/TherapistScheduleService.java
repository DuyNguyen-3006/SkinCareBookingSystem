package com.skincare_booking_system.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.skincare_booking_system.dto.request.SpecificTherapistScheduleRequest;
import com.skincare_booking_system.dto.response.SpecificTherapistScheduleResponse;
import com.skincare_booking_system.dto.response.TherapistScheduleResponse;
import com.skincare_booking_system.entities.*;
import com.skincare_booking_system.exception.AppException;
import com.skincare_booking_system.exception.ErrorCode;
import com.skincare_booking_system.repository.*;

@Service
public class TherapistScheduleService {
    private final TherapistSchedulerepository therapistSchedulerepository;
    private final TherapistRepository therapistRepository;
    private final BookingRepository bookingRepository;
    private final ShiftRepository shiftRepository;
    private final SlotRepository slotRepository;

    public TherapistScheduleService(
            TherapistSchedulerepository therapistSchedulerepository,
            TherapistRepository therapistRepository,
            BookingRepository bookingRepository,
            ShiftRepository shiftRepository,
            SlotRepository slotRepository) {
        this.therapistSchedulerepository = therapistSchedulerepository;
        this.therapistRepository = therapistRepository;
        this.bookingRepository = bookingRepository;
        this.shiftRepository = shiftRepository;
        this.slotRepository = slotRepository;
    }

    public SpecificTherapistScheduleRequest createTherapistSchedule(SpecificTherapistScheduleRequest list) {
        TherapistSchedule schedule =
                therapistSchedulerepository.getTherapistScheduleId(list.getTherapistId(), list.getWorkingDate());
        if (schedule != null) {
            throw new AppException(ErrorCode.STYLIST_SCHEDULE_EXIST);
        }

        Therapist therapist = therapistRepository
                .findById(list.getTherapistId())
                .orElseThrow(() -> new AppException(ErrorCode.THERAPIST_NOT_FOUND));

        Set<Shift> shiftSet = list.getShiftId().stream()
                .map(id -> shiftRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.SHIFT_NOT_EXIST)))
                .collect(Collectors.toSet());

        TherapistSchedule therapistSchedule = new TherapistSchedule();
        therapistSchedule.setTherapist(therapist);
        therapistSchedule.setWorkingDay(list.getWorkingDate());
        therapistSchedule.setShifts(shiftSet);
        therapistSchedulerepository.save(therapistSchedule);
        return list;
    }

    public List<SpecificTherapistScheduleResponse> getTherapistScheduleByDay(LocalDate date) {
        List<SpecificTherapistScheduleResponse> specificTherapistScheduleResponses = new ArrayList<>();

        List<TherapistSchedule> therapistSchedules = therapistSchedulerepository.getTherapistScheduleByDay(date);

        for (TherapistSchedule therapistSchedule : therapistSchedules) {

            Set<Long> shifts =
                    shiftRepository.getShiftIdByTherapistSchedule(therapistSchedule.getTherapistScheduleId());

            SpecificTherapistScheduleResponse response = new SpecificTherapistScheduleResponse();
            response.setId(therapistSchedule.getTherapistScheduleId());
            response.setTherapistName(therapistSchedule.getTherapist().getFullName()); // Lấy tên therapist
            response.setWorkingDate(therapistSchedule.getWorkingDay());
            response.setShiftId(shifts);

            specificTherapistScheduleResponses.add(response);
        }

        return specificTherapistScheduleResponses;
    }

    List<Booking> bookingByShiftNotWorking = new ArrayList<>();

    public SpecificTherapistScheduleResponse updateTherapistSchedule(
            long id, SpecificTherapistScheduleRequest request) {

        TherapistSchedule schedule = therapistSchedulerepository.findByTherapistScheduleId(id);
        Set<Shift> shifts = new HashSet<>();
        for (Long shiftId : request.getShiftId()) {
            Shift shift = shiftRepository.findByShiftId(shiftId);
            shifts.add(shift);
        }
        Set<Shift> allShifts = new HashSet<>(shiftRepository.findAll());
        allShifts.removeAll(shifts);
        for (Shift shift : allShifts) {
            List<Slot> slots = slotRepository.getSlotsInShift(shift.getShiftId());
            for (Slot slot : slots) {
                List<Booking> bookings = bookingRepository.getBookingsByTherapistScheduleAndSlotId(
                        schedule.getTherapistScheduleId(), slot.getSlotid());
                bookingByShiftNotWorking.addAll(bookings);
            }
        }
        therapistSchedulerepository.deleteSpecificSchedule(schedule.getTherapistScheduleId());
        Therapist therapist = therapistRepository.findTherapistById(id);
        schedule.setShifts(shifts);
        schedule.setTherapist(therapist);
        schedule.setWorkingDay(request.getWorkingDate());
        therapistSchedulerepository.save(schedule);
        return SpecificTherapistScheduleResponse.builder()
                .id(schedule.getTherapistScheduleId())
                .therapistName(therapist.getFullName())
                .workingDate(request.getWorkingDate())
                .shiftId(request.getShiftId())
                .build();
    }

    public TherapistScheduleResponse getTherapistSchedule(long id) {
        TherapistSchedule schedule = therapistSchedulerepository.findByTherapistScheduleId(id);
        Set<Long> shiftsId = shiftRepository.getShiftIdByTherapistSchedule(id);
        TherapistScheduleResponse response = new TherapistScheduleResponse();
        response.setId(schedule.getTherapistScheduleId());
        response.setTherapistName(schedule.getTherapist().getFullName());
        response.setTherapistId(schedule.getTherapist().getId());
        response.setWorkingDate(schedule.getWorkingDay());
        response.setShiftId(shiftsId);
        return response;
    }

    public TherapistScheduleResponse deleteStylistSchedule(long id) {
        TherapistSchedule schedule = therapistSchedulerepository.findByTherapistScheduleId(id);
        Set<Long> shiftsId = shiftRepository.getShiftIdByTherapistSchedule(id);
        for (Long shiftId : shiftsId) {
            List<Slot> slots = slotRepository.getSlotsInShift(shiftId);
            for (Slot slot : slots) {
                List<Booking> bookings = bookingRepository.getBookingsByTherapistScheduleAndShiftId(
                        schedule.getTherapistScheduleId(), slot.getSlotid());
                bookingByShiftNotWorking.addAll(bookings);
            }
        }
        TherapistScheduleResponse response = new TherapistScheduleResponse();
        response.setId(schedule.getTherapistScheduleId());
        response.setTherapistName(schedule.getTherapist().getFullName());
        response.setId(schedule.getTherapist().getId());
        response.setWorkingDate(schedule.getWorkingDay());
        response.setShiftId(shiftsId);
        therapistSchedulerepository.deleteSpecificSchedule(id);
        therapistSchedulerepository.deleteById(id);
        return response;
    }
}
