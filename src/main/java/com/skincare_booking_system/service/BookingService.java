package com.skincare_booking_system.service;

import java.sql.Date;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.skincare_booking_system.constant.BookingStatus;
import com.skincare_booking_system.dto.request.*;
import com.skincare_booking_system.dto.response.*;
import com.skincare_booking_system.entities.*;
import com.skincare_booking_system.entities.Package;
import com.skincare_booking_system.exception.AppException;
import com.skincare_booking_system.exception.ErrorCode;
import com.skincare_booking_system.repository.*;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BookingService {
    private final BookingRepository bookingRepository;
    private final ServicesRepository servicesRepository;
    private final UserRepository userRepository;
    private final TherapistRepository therapistRepository;
    private final StaffRepository staffRepository;
    private final SlotRepository slotRepository;
    private final VoucherRepository voucherRepository;
    private final TherapistSchedulerepository therapistSchedulerepository;
    private final ShiftRepository shiftRepository;
    private final TherapistService therapistService;
    private final PackageRepository packageRepository;
    private final TherapistScheduleService therapistScheduleService;
    private final EmailService emailService;
    public final UserService userService;
    private final VoucherService voucherService;
    private final PaymentRepository paymentRepository;

    public BookingService(
            BookingRepository bookingRepository,
            ServicesRepository servicesRepository,
            UserRepository userRepository,
            TherapistRepository therapistRepository,
            StaffRepository staffRepository,
            SlotRepository slotRepository,
            VoucherRepository voucherRepository,
            TherapistSchedulerepository therapistSchedulerepository,
            ShiftRepository shiftRepository,
            TherapistService therapistService,
            PackageRepository packageRepository,
            TherapistScheduleService therapistScheduleService,
            EmailService emailService,
            UserService userService,
            VoucherService voucherService,
            PaymentRepository paymentRepository) {
        this.bookingRepository = bookingRepository;
        this.servicesRepository = servicesRepository;
        this.userRepository = userRepository;
        this.therapistRepository = therapistRepository;
        this.staffRepository = staffRepository;
        this.slotRepository = slotRepository;
        this.voucherRepository = voucherRepository;
        this.therapistSchedulerepository = therapistSchedulerepository;
        this.shiftRepository = shiftRepository;
        this.therapistService = therapistService;
        this.packageRepository = packageRepository;
        this.therapistScheduleService = therapistScheduleService;
        this.emailService = emailService;
        this.userService = userService;
        this.voucherService = voucherService;
        this.paymentRepository = paymentRepository;
    }

    public Set<TherapistForBooking> getTherapistForBooking(BookingTherapist bookingTherapist) {
        Set<TherapistForBooking> therapistForBooking = new HashSet<>();
        for (Therapist therapist : therapistRepository.findByStatusTrue()) {
            TherapistForBooking response = new TherapistForBooking();
            LocalDate date = LocalDate.now();
            int year = date.getYear();
            int month = date.getMonthValue() - 1;

            if (month == 0) {
                year -= 1;
                month = 12;
            }

            String yearAndMonth = year + "-" + month;
            response.setFeedbackScore(therapistService.calculateAverageFeedback(therapist.getId(), yearAndMonth));
            response.setId(therapist.getId());
            response.setFullName(therapist.getFullName());
            response.setImage(therapist.getImage());
            therapistForBooking.add(response);
        }
        return therapistForBooking;
    }

    public List<Slot> getListSlot(BookingSlots bookingSlots) {
        List<Slot> allSlots = slotRepository.getAllSlotActive();
        List<Slot> slotToRemove = new ArrayList<>();
        List<Shift> shifts = new ArrayList<>();
        List<Shift> shiftsFromSpecificTherapistSchedule = shiftRepository.getShiftsFromSpecificTherapistSchedule(
                bookingSlots.getTherapistId(), bookingSlots.getDate());

        LocalTime lastShiftEndTime = LocalTime.MIN;
        for (Shift shift : shiftsFromSpecificTherapistSchedule) {
            if (shift.getEndTime().isAfter(lastShiftEndTime)) {
                lastShiftEndTime = shift.getEndTime(); // Lấy giờ kết thúc ca cuối cùng
            }
        }

        // Loại bỏ các slot nằm sau giờ kết thúc ca làm việc
        for (Slot slot : allSlots) {
            if (slot.getSlottime().isAfter(lastShiftEndTime)) {
                slotToRemove.add(slot);
            }
        }

        List<Shift> shiftMissingInSpecificTherapistSchedule =
                shiftMissingInSpecificTherapistSchedule(shiftsFromSpecificTherapistSchedule);

        LocalTime totalTimeServiceNewBooking = totalTimeServiceBooking(bookingSlots.getServiceId());
        slotToRemove.addAll(getSlotsExperiedTime(totalTimeServiceNewBooking, shiftsFromSpecificTherapistSchedule));
        if (!shiftMissingInSpecificTherapistSchedule.isEmpty()) {
            for (Shift shift : shiftMissingInSpecificTherapistSchedule) {
                List<Slot> slot = slotRepository.getSlotsInShift(shift.getShiftId());
                slotToRemove.addAll(slot);
            }
            if (slotToRemove.size() == allSlots.size()) {
                allSlots.removeAll(slotToRemove);
                return allSlots;
            }
        }

        List<Booking> allBookingInDay =
                bookingRepository.getBookingsByTherapistInDay(bookingSlots.getDate(), bookingSlots.getTherapistId());

        for (Slot slot : allSlots) {
            LocalDateTime localDateTime = LocalDateTime.now(ZoneId.of("Asia/Bangkok"));
            if (localDateTime.toLocalDate().isEqual(bookingSlots.getDate())) {
                if (localDateTime.toLocalTime().isAfter(slot.getSlottime())) {
                    slotToRemove.add(slot);
                } else {
                    break;
                }
            } else {
                break;
            }
        }
        if (allBookingInDay.isEmpty()) {
            allSlots.removeAll(slotToRemove);
            return allSlots;
        }

        for (Booking booking : allBookingInDay) {
            LocalTime totalTimeServiceForBooking = servicesRepository.getTotalTime(booking.getBookingId());
            // lấy ra đc slot cụ thể của từng booking vd: slot 1 -> thời gian là 7:00:00
            Slot slot = slotRepository.findSlotBySlotid(booking.getSlot().getSlotid());

            LocalTime TimeFinishBooking = slot.getSlottime()
                    .plusHours(totalTimeServiceForBooking.getHour())
                    .plusMinutes(totalTimeServiceForBooking.getMinute());

            List<Slot> list = slotRepository.getSlotToRemove(slot.getSlottime(), TimeFinishBooking.minusSeconds(1));
            slotToRemove.addAll(list);

            LocalTime minimunTimeToBooking = slot.getSlottime().minusHours(totalTimeServiceNewBooking.getHour())
                    .minusMinutes(totalTimeServiceNewBooking.getMinute());
            List<Slot> list1 = slotRepository.getSlotToRemove(minimunTimeToBooking, TimeFinishBooking);
            slotToRemove.addAll(list1);
            slotToRemove.add(slot);

            // tìm ra list ca làm mà cái booking đó thuộc về
            List<Shift> bookingBelongToShifts =
                    shiftRepository.getShiftForBooking(slot.getSlottime(), TimeFinishBooking, booking.getBookingId());
            shifts.addAll(bookingBelongToShifts);
        }
        List<Shift> shiftsReachedBookingLimit = shiftReachedBookingLimit(shifts);
        for (Shift shift : shiftsReachedBookingLimit) {
            int countTotalBookingCompleteInShift = bookingRepository.countTotalBookingCompleteInShift(
                    shift.getShiftId(), bookingSlots.getTherapistId(), bookingSlots.getDate());
            // nếu có đủ số lượng booking complete với limitBooking mà còn dư slot vẫn hiện ra
            if (countTotalBookingCompleteInShift == shift.getLimitBooking()) {
                break;
            }
            List<Slot> slots = slotRepository.getSlotsInShift(shift.getShiftId());
            // add list vừa tìm đc vào slotToRemove
            slotToRemove.addAll(slots);
        }
        allSlots.removeAll(slotToRemove);
        return allSlots;
    }

    public List<Slot> getSlotsUpdateByCustomer(BookingSlots bookingSlots, long bookingId) {
        List<Slot> allSlot = slotRepository.getAllSlotActive();
        List<Slot> slotToRemove = new ArrayList<>();
        List<Shift> shifts = new ArrayList<>();
        List<Shift> shiftsFromSpecificTherapistSchedule = shiftRepository.getShiftsFromSpecificTherapistSchedule(
                bookingSlots.getTherapistId(), bookingSlots.getDate());
        List<Shift> shiftMissingInSpecificTherapistSchedule =
                shiftMissingInSpecificTherapistSchedule(shiftsFromSpecificTherapistSchedule);
        // therapist đã có booking trong ngày
        // tính tổng thời gian để hoàn thành yêu cầu booking mới
        LocalTime totalTimeServiceNewBooking = totalTimeServiceBooking(bookingSlots.getServiceId());
        slotToRemove.addAll(getSlotsExperiedTime(totalTimeServiceNewBooking, shiftsFromSpecificTherapistSchedule));
        if (!shiftMissingInSpecificTherapistSchedule.isEmpty()) {
            for (Shift shift : shiftMissingInSpecificTherapistSchedule) {
                List<Slot> slot = slotRepository.getSlotsInShift(shift.getShiftId());
                slotToRemove.addAll(slot);
            }
            if (slotToRemove.size() == allSlot.size()) {
                allSlot.removeAll(slotToRemove);
                return allSlot;
            }
        }
        // lấy được tất cả booking trong ngày của stylist đc truyền vào
        List<Booking> allBookingInDay = bookingRepository.getBookingsByTherapistInDayForUpdate(
                bookingSlots.getDate(), bookingSlots.getTherapistId(), bookingId);
        // lấy ra tất cả các slot có trong database
        for (Slot slot : allSlot) {
            // duyệt qua từng slot xét xem coi thời gian thực có qua thời gian của slot đó chưa
            LocalTime localTime = LocalTime.now();
            LocalDate date = LocalDate.now();
            if (date.isEqual(bookingSlots.getDate())) {
                // nếu thời gian thực qua thời gian của slot đó r thì add slot đó vào 1 cái list slotToRemove
                if (localTime.isAfter(slot.getSlottime())) {
                    slotToRemove.add(slot);
                } else {
                    break;
                }
            } else {
                break;
            }
        }
        // nếu stylist đó chưa có booking nào trong ngày
        if (allBookingInDay.isEmpty()) {
            // xóa tất cả thằng có trong slotToRemove
            allSlot.removeAll(slotToRemove);
            return allSlot;
        }
        for (Booking booking : allBookingInDay) {

            LocalTime totalTimeServiceForBooking = servicesRepository.getTotalTime(booking.getBookingId());
            Slot slot = slotRepository.findSlotBySlotid(booking.getSlot().getSlotid());
            // thời gian dự kiến  hoàn thành của cái booking đó vd: 9:30:00 do slot bắt đầu là 8h
            // và thời gian hoàn thành tất cả service là 1:30
            LocalTime TimeFinishBooking = slot.getSlottime()
                    .plusHours(totalTimeServiceForBooking.getHour())
                    .plusMinutes(totalTimeServiceForBooking.getMinute());
            // Xét nếu thời gian của tất cả service của 1 booking đó có lớn hơn 1 tiếng không

            List<Slot> list = slotRepository.getSlotToRemove(slot.getSlottime(), TimeFinishBooking);
            slotToRemove.addAll(list);

            LocalTime minimunTimeToBooking = slot.getSlottime()
                    .minusHours(totalTimeServiceNewBooking.getHour())
                    .minusMinutes(totalTimeServiceNewBooking.getMinute());
            // tìm ra list chứa các slot ko thỏa và add vào list slotToRemove
            List<Slot> list1 = slotRepository.getSlotToRemove(minimunTimeToBooking, TimeFinishBooking);
            slotToRemove.addAll(list1);
            slotToRemove.add(slot); // 10 11
            // tìm ra list ca làm mà cái booking đó thuộc về
            List<Shift> bookingBelongToShifts =
                    shiftRepository.getShiftForBooking(slot.getSlottime(), TimeFinishBooking, booking.getBookingId());
            // add list vừa tìm đc vào list shifts
            shifts.addAll(bookingBelongToShifts);
        }
        // tìm xem có ca làm nào đạt limitBooking chưa
        List<Shift> shiftsReachedBookingLimit = shiftReachedBookingLimit(shifts);
        for (Shift shift : shiftsReachedBookingLimit) {
            // đếm xem có bao nhiêu booking complete trong ca làm đó
            int countTotalBookingCompleteInShift = bookingRepository.countTotalBookingCompleteInShift(
                    shift.getShiftId(), bookingSlots.getTherapistId(), bookingSlots.getDate());
            // nếu có đủ số lượng booking complete với limitBooking mà còn dư slot vẫn hiện ra
            if (countTotalBookingCompleteInShift == shift.getLimitBooking()) {
                break;
            }
            // Tìm ra đc các slots thuộc về ca làm đó
            List<Slot> slots = slotRepository.getSlotsInShift(shift.getShiftId());
            // add list vừa tìm đc vào slotToRemove
            slotToRemove.addAll(slots);
        }
        allSlot.removeAll(slotToRemove);
        return allSlot;
    }

    private Set<TherapistForBooking> getTherapistByDateWorkingAndShift(LocalDate date, Long slotId) {

        Shift shift = shiftRepository.getShiftBySlot(slotId);
        List<Therapist> therapistsList = therapistRepository.getTherapistForBooking(date, shift.getShiftId());

        Set<TherapistForBooking> therapistsForBooking = new HashSet<>();
        for (Therapist therapist : therapistsList) {
            TherapistForBooking therapistBooking = new TherapistForBooking();
            therapistBooking.setId(therapist.getId());
            therapistBooking.setFullName(therapist.getFullName());
            therapistBooking.setImage(therapist.getImage());
            therapistBooking.setFeedbackScore(therapistService.calculateAverageFeedback(therapist.getId(), "2025-03"));
            therapistsForBooking.add(therapistBooking);
        }

        return therapistsForBooking;
    }

    public Set<TherapistForBooking> getTherapistWhenUpdateBookingByStaff(
            AssignNewTherapistForBooking newTherapistForBooking) {
        Long slotId = newTherapistForBooking.getSlotId();
        LocalDate date = newTherapistForBooking.getDate();
        Set<TherapistForBooking> therapistsForBooking = getTherapistByDateWorkingAndShift(date, slotId);

        Slot slotBookingUpdate = slotRepository.findSlotBySlotid(newTherapistForBooking.getSlotId());
        // tính tổng thời gian hoàn thành các services của booking mới
        LocalTime totalServiceTimeForNewBooking = totalTimeServiceBooking(newTherapistForBooking.getServiceId());

        List<TherapistForBooking> therapistsToRemove = new ArrayList<>();
        for (TherapistForBooking therapist : therapistsForBooking) {
            List<Booking> bookings =
                    bookingRepository.getBookingsByTherapistInDay(newTherapistForBooking.getDate(), therapist.getId());

            Booking bookingNearestOverTime = bookingRepository.bookingNearestOverTime(
                    therapist.getId(), slotBookingUpdate.getSlottime(), newTherapistForBooking.getDate());
            Booking bookingNearestBeforeTime = bookingRepository.bookingNearestBeforeTime(
                    therapist.getId(), slotBookingUpdate.getSlottime(), newTherapistForBooking.getDate());
            Booking bookingAtTimeUpdate = bookingRepository.bookingAtTime(
                    slotBookingUpdate.getSlotid(), therapist.getId(), newTherapistForBooking.getDate());
            LocalTime timeToCheckValid = slotBookingUpdate
                    .getSlottime()
                    .plusHours(totalServiceTimeForNewBooking.getHour())
                    .plusMinutes(totalServiceTimeForNewBooking.getMinute());
            if (bookingAtTimeUpdate != null) {
                therapistsToRemove.add(therapist);
            }
            if (bookingNearestOverTime != null) {
                // lấy đc thời gian của cái booking có sẵn của stylist đó
                Slot slotTimeBooking = slotRepository.findSlotBySlotid(
                        bookingNearestOverTime.getSlot().getSlotid());
                // nếu tổng thời gian hoàn thành booking mới đó mà lố thời gian của booking có sẵn thì stylist đó ko
                // thỏa
                if (timeToCheckValid.isAfter(slotTimeBooking.getSlottime())) {
                    therapistsToRemove.add(therapist);
                }
            }
            if (bookingNearestBeforeTime != null) {
                LocalTime totalTimeServiceForBooking =
                        servicesRepository.getTotalTime(bookingNearestBeforeTime.getBookingId());
                // lấy đc thời gian của cái booking có sẵn của stylist đó
                Slot slotTimeBooking = slotRepository.findSlotBySlotid(
                        bookingNearestBeforeTime.getSlot().getSlotid());
                LocalTime totalTimeFinishBooking = slotTimeBooking
                        .getSlottime()
                        .plusHours(totalTimeServiceForBooking.getHour())
                        .plusMinutes(totalTimeServiceForBooking.getMinute());

                // nếu tổng thời gian hoàn thành booking mới đó mà lố thời gian của booking có sẵn thì stylist đó ko
                // thỏa
                if (totalTimeFinishBooking.isAfter(slotBookingUpdate.getSlottime())) {
                    therapistsToRemove.add(therapist);
                }
            }
            if (!bookings.isEmpty()) {
                boolean checkStylist = shiftsHaveFullBooking(bookings, slotBookingUpdate);
                if (checkStylist) {
                    therapistsToRemove.add(therapist);
                }
            }
        }
        therapistsForBooking.removeAll(therapistsToRemove);
        return therapistsForBooking;
    }

    public BookingRequest createNewBooking(BookingRequest request) {
        User user = userRepository.findUserById(request.getUserId());
        Set<Services> services = new HashSet<>();
        for (Long id : request.getServiceId()) {
            Services service = servicesRepository.getServiceById(id);
            services.add(service);
        }
        Slot slot = slotRepository.findSlotBySlotid(request.getSlotId());
        BookingSlots bookingSlots = new BookingSlots();
        bookingSlots.setServiceId(request.getServiceId());
        bookingSlots.setDate(request.getBookingDate());
        bookingSlots.setTherapistId(request.getTherapistId());
        System.out.println("BookingSlots input: " + bookingSlots);
        List<Slot> slotAvailable = getListSlot(bookingSlots);
        int count = 0;
        for (Slot s : slotAvailable) {
            if (s.getSlotid() == request.getSlotId()) {
                count++;
            }
        }
        if (count == 0) {
            throw new AppException(ErrorCode.SLOT_NOT_VALID);
        }
        Voucher voucher = voucherRepository.findVoucherByVoucherId(request.getVoucherId());
        if (voucher != null) {
            voucherService.useVoucher(voucher.getVoucherCode());
        }
        TherapistSchedule therapistSchedule =
                therapistSchedulerepository.getTherapistScheduleId(request.getTherapistId(), request.getBookingDate());
        Booking checkBookingExist =
                bookingRepository.getBySlotSlotidAndBookingDayAndTherapistScheduleTherapistScheduleId(
                        slot.getSlotid(), request.getBookingDate(), therapistSchedule.getTherapistScheduleId());
        if (checkBookingExist != null) {
            throw new AppException(ErrorCode.BOOKING_EXIST);
        }
        Booking booking = new Booking();
        booking.setBookingDay(request.getBookingDate());
        booking.setUser(user);
        booking.setSlot(slot);
        booking.setServices(services);
        booking.setVoucher(voucher);
        booking.setTherapistSchedule(therapistSchedule);
        booking.setStatus(BookingStatus.PENDING);
        Booking newBooking = bookingRepository.save(booking);
        for (Services service : services) {
            bookingRepository.updateBookingDetail(
                    service.getPrice(), newBooking.getBookingId(), service.getServiceId());
        }
        User currentUser = currentUser();
        CreateNewBookingSuccess success = new CreateNewBookingSuccess();
        success.setDate(booking.getBookingDay());
        success.setTime(booking.getSlot().getSlottime());
        success.setTo(currentUser.getEmail());
        success.setSubject("Create booking successfully");
        success.setTherapistName(booking.getTherapistSchedule().getTherapist().getFullName());

        emailService.sendMailInformBookingSuccess(success);

        return request;
    }

    public BookingRequest updateBooking(long bookingId, BookingRequest request) {
        Booking booking = bookingRepository.findBookingByBookingId(bookingId);
        if (booking == null) {
            throw new AppException(ErrorCode.BOOKING_NOT_FOUND);
        }
        User user = userRepository.findUserById(request.getUserId());
        if (user == null) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }
        Set<Services> services = new HashSet<>();
        for (Long id : request.getServiceId()) {
            Services service = servicesRepository.getServiceById(id);
            services.add(service);
        }
        Slot slot = slotRepository.findSlotBySlotid(request.getSlotId());
        BookingSlots bookingSlots = new BookingSlots();
        bookingSlots.setServiceId(request.getServiceId());
        bookingSlots.setDate(request.getBookingDate());
        bookingSlots.setTherapistId(request.getTherapistId());
        List<Slot> slotAvailable = getSlotsUpdateByCustomer(bookingSlots, bookingId);
        int count = 0;
        for (Slot s : slotAvailable) {
            if (s.getSlotid() == request.getSlotId()) {
                count++;
            }
        }
        if (count == 0) {
            throw new AppException(ErrorCode.SLOT_NOT_VALID);
        }
        bookingRepository.deleteBookingDetail(booking.getBookingId());
        if (booking.getVoucher() != null) {
            Voucher oldVoucher = voucherRepository.findVoucherByVoucherId(
                    booking.getVoucher().getVoucherId());
            oldVoucher.setQuantity(oldVoucher.getQuantity() + 1);
        }
        Voucher newVoucher = voucherRepository.findVoucherByVoucherId(request.getVoucherId());
        if (newVoucher != null) {
            newVoucher.setQuantity(newVoucher.getQuantity() - 1);
        }
        TherapistSchedule therapistSchedule =
                therapistSchedulerepository.getTherapistScheduleId(request.getTherapistId(), request.getBookingDate());
        booking.setBookingDay(request.getBookingDate());
        booking.setUser(user);
        booking.setSlot(slot);
        booking.setServices(services);
        booking.setVoucher(newVoucher);
        booking.setTherapistSchedule(therapistSchedule);
        booking.setStatus(BookingStatus.PENDING);
        bookingRepository.save(booking);

        for (Services service : services) {
            bookingRepository.updateBookingDetail(service.getPrice(), booking.getBookingId(), service.getServiceId());
        }

        Booking bookingToRemove = new Booking();
        if (!therapistScheduleService.bookingByShiftNotWorking.isEmpty()) {
            for (Booking booking1 : therapistScheduleService.bookingByShiftNotWorking) {
                if (booking.getBookingId() == booking1.getBookingId()) {
                    bookingToRemove = booking1;
                    break;
                }
            }
        }
        therapistScheduleService.bookingByShiftNotWorking.remove(bookingToRemove);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Staff currentStaff = staffRepository
                .findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        if (currentStaff != null) {
            ChangeTherapist success = new ChangeTherapist();
            success.setDate(booking.getBookingDay());
            success.setTime(booking.getSlot().getSlottime());
            success.setTo(booking.getUser().getEmail());
            success.setSubject("Change Stylist");
            success.setTherapistName(
                    booking.getTherapistSchedule().getTherapist().getFullName());
            emailService.sendMailChangeTherapist(success);
            System.out.println("Successfully changed the stylist in service");
        }
        return request;
    }

    public String deleteBooking(long id) {
        Booking booking = bookingRepository.findBookingByBookingId(id);
        if (booking == null) {
            throw new AppException(ErrorCode.BOOKING_NOT_FOUND);
        }
        if (booking.getVoucher() != null) {
            Voucher voucher = voucherRepository.findVoucherByVoucherId(
                    booking.getVoucher().getVoucherId());
            voucher.setQuantity(voucher.getQuantity() + 1);
        }
        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
        return "Booking deleted";
    }

    public BookingResponse getBookingById(long bookingId) {
        Booking booking = bookingRepository.findBookingByBookingId(bookingId);

        Set<Services> services = servicesRepository.getServiceForBooking(bookingId);
        Set<Long> serviceId = new HashSet<>();
        for (Services service : services) {
            serviceId.add(service.getServiceId());
        }

        Therapist therapist = therapistRepository.findTherapistById(
                booking.getTherapistSchedule().getTherapist().getId());
        BookingResponse bookingResponse = new BookingResponse();
        bookingResponse.setId(booking.getBookingId());
        bookingResponse.setUserPhone(booking.getUser().getPhone());
        bookingResponse.setDate(booking.getBookingDay());
        bookingResponse.setTime(booking.getSlot().getSlottime());
        bookingResponse.setUserId(booking.getUser().getId());
        bookingResponse.setUserName(
                booking.getUser().getFirstName() + " " + booking.getUser().getLastName());
        bookingResponse.setServiceId(serviceId);
        bookingResponse.setTherapistName(therapist.getFullName());
        bookingResponse.setStatus(booking.getStatus());
        if (booking.getVoucher() != null) {
            bookingResponse.setVoucherCode(booking.getVoucher().getVoucherCode());
        }

//        Set<Package> packages = packageRepository.getPackageForBooking(bookingId);
//        if (packages != null) {
//            Set<Long> packageId = new HashSet<>();
//            for (Package p : packages) {
//                packageId.add(p.getPackageId());
//            }
//            bookingResponse.setPackageId(packageId);
//        }
        return bookingResponse;
    }

    public boolean shiftsHaveFullBooking(List<Booking> bookings, Slot slotBookingUpdate) {
        List<Shift> shifts = new ArrayList<>();

        for (Booking booking : bookings) {
            Slot slot = slotRepository.findSlotBySlotid(booking.getSlot().getSlotid());
            LocalTime totalTimeServiceForBooking = servicesRepository.getTotalTime(booking.getBookingId());
            LocalTime timeFinishBooking = slot.getSlottime()
                    .plusHours(totalTimeServiceForBooking.getHour())
                    .plusMinutes(totalTimeServiceForBooking.getMinute());
            List<Shift> shiftBookingBelongTo =
                    shiftRepository.getShiftForBooking(slot.getSlottime(), timeFinishBooking, booking.getBookingId());
            shifts.addAll(shiftBookingBelongTo);
        }
        List<Shift> shiftReachedBookingLimit = shiftReachedBookingLimit(shifts);
        for (Shift shift : shiftReachedBookingLimit) {
            Shift shiftBySlot = shiftRepository.getShiftBySlot(slotBookingUpdate.getSlotid());
            if (shift.getShiftId() == shiftBySlot.getShiftId()) {
                return true;
            }
        }
        return false;
    }

    private List<CustomerBookingResponse> getBookingResponses(List<Booking> status) {
        return status.stream()
                .map(booking -> {
                    CustomerBookingResponse response = new CustomerBookingResponse();
                    response.setBookingId(booking.getBookingId());
                    response.setTherapistName(
                            booking.getTherapistSchedule() != null
                                    ? booking.getTherapist().getFullName()
                                    : null);
                    response.setBookingDate(booking.getBookingDay());
                    response.setBookingTime(
                            booking.getSlot() != null ? booking.getSlot().getSlottime() : null);
                    Set<ServiceCusResponse> serviceDTOs = booking.getServices().stream()
                            .map(service -> new ServiceCusResponse(service.getServiceName()))
                            .collect(Collectors.toSet());
                    response.setServiceName(serviceDTOs);

                    response.setStatus(booking.getStatus());
                    return response;
                })
                .collect(Collectors.toList());
    }

    public List<CustomerBookingResponse> getBookingByStatusPendingByCustomer(Long userId) {
        List<Booking> bookings = bookingRepository.getBookingsByUserIdAndStatus(userId, BookingStatus.PENDING.name());

        // Gán dịch vụ cho từng booking
        bookings.forEach(booking -> {
            Set<Services> services = servicesRepository.getServiceForBooking(booking.getBookingId());
            booking.setServices(services);
        });

        return getBookingResponses(bookings);
    }

    public List<CustomerBookingResponse> getBookingByStatusCompletedByCustomer(Long userId) {
        User user = new User();
        user.setId(userId);
        List<Booking> status = new ArrayList<>();
        List<Booking> bookings = bookingRepository.getBookingsByUserIdAndStatus(userId, BookingStatus.COMPLETED.name());
        for (Booking booking : bookings) {
            Set<Services> service = servicesRepository.getServiceForBooking(booking.getBookingId());
            booking.setServices(service);
            status.add(booking);
        }
        return getBookingResponses(status);
    }

    public String checkIn(long bookingId) {
        Booking booking = bookingRepository.findBookingByBookingId(bookingId);
        if (booking == null) {
            throw new AppException(ErrorCode.BOOKING_NOT_FOUND);
        }
        booking.setStatus(BookingStatus.IN_PROGRESS);
        bookingRepository.save(booking);
        return "check-in success";
    }

    public PaymentResponse finishedService(long bookingId) {
        Booking booking = bookingRepository.findBookingByBookingId(bookingId);
        if (booking == null) {
            throw new AppException(ErrorCode.BOOKING_NOT_FOUND);
        }
        Set<Services> services = booking.getServices();
        double totalAmount = 0;
        Set<PaymentServiceResponse> serviceResponses = new HashSet<>();
        for (Services service : services) {
            totalAmount += service.getPrice();
            serviceResponses.add(
                    new PaymentServiceResponse(service.getServiceName(), service.getImgUrl(), service.getPrice()));
        }
        String voucherCode = null;
        if (booking.getVoucher() != null) {
            double discount = booking.getVoucher().getPercentDiscount();
            totalAmount -= totalAmount * discount / 100;
            voucherCode = booking.getVoucher().getVoucherCode();
        }
        Payment existingPayment = paymentRepository.findPaymentByBooking(booking);
        if (existingPayment != null) {
            // Delete the existing payment
            paymentRepository.delete(existingPayment);
        }
        String therapistName = booking.getTherapistSchedule().getTherapist().getFullName();

        Payment payment = Payment.builder()
                .paymentAmount(totalAmount)
                .paymentDate(LocalDate.now())
                .paymentStatus("Pending")
                .booking(booking)
                .build();
        paymentRepository.save(payment);
        return new PaymentResponse(
                booking.getBookingId(),
                booking.getBookingDay(),
                booking.getUser().getFirstName(),
                therapistName,
                serviceResponses,
                voucherCode,
                totalAmount);
    }

    public String checkout(String transactionId, Long bookingId) {
        Payment payment = null;
        Booking booking = null;
        if (transactionId != null && !transactionId.isEmpty()) {
            payment = paymentRepository.findByTransactionId(transactionId);
            if (payment == null) {
                throw new AppException(ErrorCode.BOOKING_NOT_FOUND);
            }
            booking = payment.getBooking();
        } else if (bookingId != null) {
            booking = bookingRepository
                    .findById(bookingId)
                    .orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_FOUND));
            payment = booking.getPayment();
        } else {
            throw new AppException(ErrorCode.EXCEPTION);
        }

        booking.setStatus(BookingStatus.COMPLETED);
        bookingRepository.save(booking);

        booking.getPayment().setPaymentStatus("Completed");
        if (booking.getPayment().getPaymentMethod() == null) {
            booking.getPayment().setPaymentMethod("Cash");
            booking.getPayment().setTransactionId(null);
        }
        paymentRepository.save(payment);

        return "Check-out success";
    }

    private List<Shift> shiftMissingInSpecificTherapistSchedule(List<Shift> shifts) {
        List<Shift> allShift = shiftRepository.findAll();
        allShift.removeAll(shifts);
        return allShift;
    }

    private LocalTime totalTimeServiceBooking(Set<Long> serviceId) {
        LocalTime totalTimeDuration = LocalTime.of(0, 0, 0);
        for (Long id : serviceId) {
            Services service = servicesRepository.getServiceById(id);
            LocalTime duration = service.getDuration();
            totalTimeDuration = totalTimeDuration.plusHours(duration.getHour()).plusMinutes(duration.getMinute());
        }
        return totalTimeDuration;
    }

    private List<Slot> getSlotsExperiedTime(LocalTime time, List<Shift> shifts) {
        Shift shift = shifts.get(0);
        List<Slot> slotsToRemove = new ArrayList<>();

        List<Slot> slots = slotRepository.getSlotsInShift(shift.getShiftId());
        for (Slot slot : slots) {
            LocalTime totalTime = slot.getSlottime().plusHours(time.getHour()).plusMinutes(time.getMinute());
            if (totalTime.isBefore(slot.getSlottime())) {
                // Thời gian totalTime đã vượt qua ngày mới
                totalTime = totalTime.plusHours(24);
            }
            if (totalTime.isAfter(shift.getEndTime()) || totalTime.isBefore(slot.getSlottime())) {
                slotsToRemove.add(slot);
            }
        }
        return slotsToRemove;
    }

    private List<Shift> shiftReachedBookingLimit(List<Shift> shifts) {
        List<Shift> list = new ArrayList<>();
        // tạo set vì trong set ko có phần tử trùng lặp
        Set<Shift> set = new HashSet<>(shifts);
        for (Shift shift : set) {
            // đếm số lần shift xuất hiện trong shifts
            int totalBookingInShift = Collections.frequency(shifts, shift);
            // nếu totalBookingInShift == limit booking thì add shift đó vào list
            if (totalBookingInShift == shift.getLimitBooking()) {
                list.add(shift);
            }
        }
        return list;
    }

    private User currentUser() {
        var context = SecurityContextHolder.getContext();
        var authentication = context.getAuthentication();

        String username = authentication.getName();
        return userRepository.findByUsername(username).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
    }

    public BookingRequest updateBookingWithService(Long bookingId, Set<Long> newServiceIds) {
        log.info("Updating booking with new services...");
        log.info("Booking ID: {}", bookingId);
        log.info("New Service IDs: {}", newServiceIds);

        Booking booking = bookingRepository
                .findBookingById(bookingId)
                .orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_FOUND));
        log.info("Found booking: {}", booking);

        Set<Long> currentServiceIds =
                booking.getServices().stream().map(Services::getServiceId).collect(Collectors.toSet());
        log.info("Current Service IDs: {}", currentServiceIds);

        if (currentServiceIds.equals(newServiceIds)) {
            log.warn("The provided services are already booked.");
            throw new AppException(ErrorCode.SERVICES_ALREADY_BOOKED);
        }

        Optional<Booking> nextBooking = bookingRepository.findNextBookingSameDay(
                booking.getTherapistSchedule().getTherapist().getId(),
                booking.getSlot().getSlotid(),
                booking.getBookingDay());
        log.info(
                "Stylist Account ID: {}",
                booking.getTherapistSchedule().getTherapist().getId());
        log.info("Current Slot Time: {}", booking.getSlot().getSlotid());
        log.info("Booking Day: {}", booking.getBookingDay());
        log.info("Next booking: {}", nextBooking);

        LocalTime currentTotalDuration = totalTimeServiceBooking(currentServiceIds);
        log.info("Current Total Duration: {}", currentTotalDuration);

        LocalTime newServicesDuration = totalTimeServiceBooking(newServiceIds);
        log.info("New Services Duration: {}", newServicesDuration);

        if (nextBooking.isPresent()) {
            LocalTime nextSlotTime = nextBooking.get().getSlot().getSlottime();
            LocalTime currentSlotTime = booking.getSlot().getSlottime();
            long availableTime = Duration.between(currentSlotTime, nextSlotTime).toMinutes();
            log.info("Available Time until next booking: {} minutes", availableTime);

            if (newServicesDuration.getHour() * 60 + newServicesDuration.getMinute() > availableTime) {
                log.warn("Stylist is unavailable for the requested services due to a scheduling conflict.");
                throw new AppException(ErrorCode.STYLIST_UNAVAILABLE);
            }
        }

        List<Long> shiftIds = therapistSchedulerepository.findShiftIdsByTherapistScheduleAndWorkingDay(
                booking.getTherapistSchedule().getTherapistScheduleId(),
                booking.getTherapistSchedule().getWorkingDay().toString());
        Collections.sort(shiftIds);
        log.info(" Shift: {}", shiftIds);

        // Hàm này là để tính toán ca đó có liên tiếp hay không
        // nếu ca đó liên tiếp thì sẽ chạy else còn nếu không liên tiếp sẽ chạy if
        // VD shift_id là 1 và 2 thì là else hoặc shift_id là 1 và 3 thì if
        // liên tiếp là false
        // ko liên tiếp là true;
        boolean isNonConsecutive = false;
        for (int i = 0; i < shiftIds.size() - 1; i++) {
            if (shiftIds.get(i) + 1 != shiftIds.get(i + 1)) {
                isNonConsecutive = true;
                break;
            }
        }

        log.info(" isNonConsecutive: {}", isNonConsecutive);

        if (isNonConsecutive) {
            // lấy ca làm việc
            List<Shift> stylistShifts = shiftRepository.getShiftsFromSpecificTherapistSchedule(
                    booking.getTherapistSchedule().getTherapist().getId(), booking.getBookingDay());
            stylistShifts.sort(Comparator.comparing(Shift::getStartTime));

            // Lấy thời gian booking đặt
            LocalTime bookingStartTime = booking.getSlot().getSlottime();
            // thời gian booking xong
            LocalTime bookingEndTime = bookingStartTime
                    .plusHours(newServicesDuration.getHour())
                    .plusMinutes(newServicesDuration.getMinute());
            log.info("Booking start time: {}", bookingStartTime);
            log.info("Booking end time: {}", bookingEndTime);

            LocalTime startTime = LocalTime.MIN;
            LocalTime endTime = LocalTime.MIN;
            LocalTime nextStartTime = LocalTime.MIN;
            LocalTime nextEndTime = LocalTime.MIN;
            if (!stylistShifts.isEmpty()) {
                startTime = stylistShifts.get(0).getStartTime();
                endTime = stylistShifts.get(0).getEndTime();
                log.info("First shift start time: {}", startTime);
                log.info("First shift end time: {}", endTime);

                // If there is a second shift, get the next startTime and next endTime
                if (stylistShifts.size() > 1) {
                    nextStartTime = stylistShifts.get(1).getStartTime();
                    nextEndTime = stylistShifts.get(1).getEndTime();
                    log.info("Next shift start time: {}", nextStartTime);
                    log.info("Next shift end time: {}", nextEndTime);
                }
            }

            if ((bookingEndTime.isAfter(endTime) && bookingEndTime.isBefore(nextStartTime))
                    || bookingEndTime.isAfter(nextEndTime)
                    || bookingEndTime.isBefore(LocalTime.of(6, 0))) {
                throw new AppException(ErrorCode.STYLIST_UNAVAILABLE);
            } else {
                log.info("Booking end time is within shift time.");
            }
            // xóa dịch vụ cũ đi
            booking.getServices().clear();
            List<Services> newServices = servicesRepository.findByServiceIdIn(new ArrayList<>(newServiceIds));
            if (newServices.isEmpty()) {
                log.error("No services found with the provided IDs.");
                throw new AppException(ErrorCode.SERVICE_NOT_FOUND);
            }
            booking.getServices().addAll(newServices);
            log.info("Final Booking before saving: {}", booking);

        } else {
            // Liên hàm này sẽ xét ca làm của Stylist xem có xung đột hay không

            // Lấy giờ kết thúc của ca làm việc của stylist trong ngày booking
            LocalTime shiftEndTime = therapistSchedulerepository
                    .findShiftEndTime(booking.getTherapistSchedule().getTherapistScheduleId(), booking.getBookingDay())
                    .orElseThrow(() -> new AppException(ErrorCode.SHIFT_NOT_EXIST));

            // Lấy thời gian bắt đầu của booking (slot time)
            LocalTime bookingStartTime = booking.getSlot().getSlottime();

            // Tính toán thời gian kết thúc dự kiến của booking sau khi thêm dịch vụ mới
            LocalTime bookingEndTime = bookingStartTime
                    .plusHours(newServicesDuration.getHour())
                    .plusMinutes(newServicesDuration.getMinute());
            log.info("Booking start time: {}", bookingStartTime);
            log.info("Booking end time: {}", bookingEndTime);
            log.info("Shift end time: {}", shiftEndTime);

            // Kiểm tra nếu thời gian kết thúc booking vượt quá giờ kết thúc ca làm việc của stylist
            // bookingEndTime.isAfter(shiftEndTime)bắt giữ các trường hợp bookingEndTimevượt quá 23:00.
            // bookingEndTime.isBefore(LocalTime.of(6, 0))(giả sử các nhà tạo mẫu tóc không làm việc sau nửa đêm cho đến
            // sáng sớm)
            // xử lý các trường hợp bookingEndTime kéo dài đến tận sáng sớm ngày hôm sau, điều này cũng được coi là
            // ngoài phạm vi.
            if (bookingEndTime.isAfter(shiftEndTime) || bookingEndTime.isBefore(LocalTime.of(6, 0))) {
                log.error("Booking end time {} exceeds shift end time {}", bookingEndTime, shiftEndTime);
                throw new AppException(ErrorCode.STYLIST_UNAVAILABLE);
            } else {
                log.info("Booking end time is within shift time.");
            }
        }

        // xóa dịch vụ cũ đi
        booking.getServices().clear();
        List<Services> newServices = servicesRepository.findByServiceIdIn(new ArrayList<>(newServiceIds));
        if (newServices.isEmpty()) {
            log.error("No services found with the provided IDs.");
            throw new AppException(ErrorCode.SERVICE_NOT_FOUND);
        }
        booking.getServices().addAll(newServices);
        log.info("Final Booking before saving: {}", booking);

        // lưu dịch vụ ới
        bookingRepository.save(booking);
        for (Services service : newServices) {
            bookingRepository.updateBookingDetail(service.getPrice(), booking.getBookingId(), service.getServiceId());
            log.info(
                    "Updated booking detail with service ID: {}, price: {}",
                    service.getServiceId(),
                    service.getPrice());
        }

        // set xuống
        BookingRequest response = new BookingRequest();
        response.setUserId(booking.getUser().getId());
        response.setSlotId(booking.getSlot().getSlotid());
        response.setServiceId(
                booking.getServices().stream().map(Services::getServiceId).collect(Collectors.toSet()));
        response.setTherapistId(booking.getTherapistSchedule().getTherapist().getId());
        response.setBookingDate(booking.getBookingDay());
        response.setVoucherId(
                booking.getVoucher() != null ? booking.getVoucher().getVoucherId() : "");
        log.info("Returning updated booking response: {}", response);
        return response;
    }

    public List<TotalMoneyByBookingDay> totalMoneyByBookingDayInMonth(int month) {
        List<Object[]> objects = bookingRepository.getTotalMoneyByBookingDay(month);
        List<TotalMoneyByBookingDay> responses = new ArrayList<>();
        for (Object[] object : objects) {
            LocalDate date = ((Date) object[0]).toLocalDate();
            double totalMoney = (double) object[1];
            TotalMoneyByBookingDay totalMoneyByBookingDay = new TotalMoneyByBookingDay(date, totalMoney);
            responses.add(totalMoneyByBookingDay);
        }
        return responses;
    }

    public Long countAllBookingsInMonth(int month) {
        return bookingRepository.countAllBookingsInMonth(month);
    }

    public double totalMoneyByMonth(int month) {
        return bookingRepository.getTotalMoneyInMonth(month);
    }

    public String checkBookingStatus(long bookingId) {
        Booking booking = bookingRepository.checkBookingStatus(bookingId);
        if (booking == null) {
            return "false";
        }
        return "true";
    }

    public Long countAllBookingsCompleted(String yearAndMonth) {
        String arr[] = yearAndMonth.split("-");
        int year = Integer.parseInt(arr[0]);
        int month = Integer.parseInt(arr[1]);

        return bookingRepository.countAllBookingsCompleted(year, month);
    }
}
