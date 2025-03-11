package com.skincare_booking_system.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

import com.skincare_booking_system.dto.response.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.skincare_booking_system.constant.BookingStatus;
import com.skincare_booking_system.dto.request.*;
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
    //    private final PaymentRepository paymentRepository;
    private final EmailService emailService;
    public final UserService userService;
    private final VoucherService voucherService;

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
            VoucherService voucherService) {
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
            LocalTime localTime = LocalTime.now();
            LocalDate date = LocalDate.now();
            if (date.isEqual(bookingSlots.getDate())) {
                if (localTime.isAfter(slot.getSlottime())) {
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

            List<Slot> list1 = slotRepository.getSlotToRemove(slot.getSlottime(), TimeFinishBooking);
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

    private Set<TherapistForBooking> getTherapistByDateWorkingAndShift(
            AssignNewTherapistForBooking newTherapistForBooking) {

        Shift shift = shiftRepository.getShiftBySlot(newTherapistForBooking.getSlotId());
        Set<Therapist> therapists =
                therapistRepository.getTherapistForBooking(newTherapistForBooking.getDate(), shift.getShiftId());

        Set<TherapistForBooking> therapistsForBooking = new HashSet<>();
        for (Therapist therapist : therapists) {
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

        Set<TherapistForBooking> therapistsForBooking = getTherapistByDateWorkingAndShift(newTherapistForBooking);

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
            Services service = servicesRepository.getServiceByServiceId(id);
            services.add(service);
        }
        Set<Package> packages = new HashSet<>();
        if (request.getPackageId() != null && !request.getPackageId().isEmpty()) {
            for (Long id : request.getPackageId()) {
                Package packageGet = packageRepository.getPackageByPackageId(id);
                packages.add(packageGet);
            }
        }
        Slot slot = slotRepository.findSlotBySlotid(request.getSlotId());
        BookingSlots bookingSlots = new BookingSlots();
        bookingSlots.setServiceId(request.getServiceId());
        bookingSlots.setPackageId(request.getPackageId());
        bookingSlots.setDate(request.getBookingDate());
        bookingSlots.setTherapistId(request.getTherapistId());
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
        booking.setPackages(packages);
        booking.setVoucher(voucher);
        booking.setTherapistSchedule(therapistSchedule);
        booking.setStatus(BookingStatus.PENDING);
        Booking newBooking = bookingRepository.save(booking);
        for (Services service : services) {
            bookingRepository.updateBookingDetail(service.getPrice(), newBooking.getBookingId(), service.getServiceId());
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
            Services service = servicesRepository.getServiceByServiceId(id);
            services.add(service);
        }
        Set<Package> packages = new HashSet<>();
        for (Long id : request.getPackageId()) {
            Package packageGet = packageRepository.getPackageByPackageId(id);
            packages.add(packageGet);
        }
        Slot slot = slotRepository.findSlotBySlotid(request.getSlotId());
        BookingSlots bookingSlots = new BookingSlots();
        bookingSlots.setServiceId(request.getServiceId());
        bookingSlots.setPackageId(request.getPackageId());
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
        booking.setPackages(packages);
        booking.setVoucher(newVoucher);
        booking.setTherapistSchedule(therapistSchedule);
        booking.setStatus(BookingStatus.PENDING);
        bookingRepository.save(booking);

        for (Services service : services) {
            bookingRepository.updateBookingDetail(service.getPrice(), booking.getBookingId(), service.getServiceId());
        }
        for (Package packageItem : packages) {
            bookingRepository.updateBookingDetailForPackage(
                    packageItem.getPackageFinalPrice(), booking.getBookingId(), packageItem.getPackageId());
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
            emailService.sendMailChangeStylist(success);
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
        bookingResponse.setDate(booking.getBookingDay());
        bookingResponse.setTime(booking.getSlot().getSlottime());
        bookingResponse.setUserId(booking.getUser().getId());
        bookingResponse.setUserName(
                booking.getUser().getFirstName() + " " + booking.getUser().getLastName());
        bookingResponse.setServiceId(serviceId);
        bookingResponse.setTherapistName(therapist.getFullName());

        if (booking.getVoucher() != null) {
            bookingResponse.setVoucherCode(booking.getVoucher().getVoucherCode());
        }

        Set<Package> packages = packageRepository.getPackageForBooking(bookingId);
        if (packages != null) {
            Set<Long> packageId = new HashSet<>();
            for (Package p : packages) {
                packageId.add(p.getPackageId());
            }
            bookingResponse.setPackageId(packageId);
        }
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
        for(Shift shift : shiftReachedBookingLimit){
            Shift shiftBySlot = shiftRepository.getShiftBySlot(slotBookingUpdate.getSlotid());
            if(shift.getShiftId() == shiftBySlot.getShiftId()){
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
                    response.setTherapistName( booking.getTherapistSchedule() != null ? booking.getTherapist().getFullName() : null);
                    response.setBookingDate(booking.getBookingDay());
                    response.setBookingTime(booking.getSlot() != null ? booking.getSlot().getSlottime() : null);
                    Set<ServiceCusResponse> serviceDTOs = booking.getServices().stream()
                            .map(service -> new ServiceCusResponse(
                                    service.getServiceName()
                            ))
                            .collect(Collectors.toSet());
                    response.setServiceName(serviceDTOs);

                    Set<PackageCusResponse> packageDTOs = (booking.getPackages() != null) ?
                            booking.getPackages().stream()
                                    .map(pkg -> new PackageCusResponse(pkg.getPackageName()))
                                    .collect(Collectors.toSet())
                            : Collections.emptySet(); // Nếu không có package, trả về Set rỗng

                    response.setPackages(packageDTOs);

                    response.setStatus(booking.getStatus());
                    return response;
                })
                .collect(Collectors.toList());
    }

    private List<Shift> shiftMissingInSpecificTherapistSchedule(List<Shift> shifts) {
        List<Shift> allShift = shiftRepository.findAll();
        allShift.removeAll(shifts);
        return allShift;
    }

    private LocalTime totalTimeServiceBooking(Set<Long> serviceId) {
        LocalTime totalTimeDuration = LocalTime.of(0, 0, 0);
        for (Long id : serviceId) {
            Services service = servicesRepository.getServiceByServiceId(id);
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
        User user = (User) context.getAuthentication().getPrincipal();
        return user;
    }
}
