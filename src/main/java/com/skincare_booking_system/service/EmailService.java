package com.skincare_booking_system.service;

import com.skincare_booking_system.constant.BookingStatus;
import com.skincare_booking_system.dto.request.ReminderBooking;
import com.skincare_booking_system.entities.Booking;
import com.skincare_booking_system.repository.BookingRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import jakarta.transaction.Transactional;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.skincare_booking_system.dto.request.ChangeTherapist;
import com.skincare_booking_system.dto.request.CreateNewBookingSuccess;
import com.skincare_booking_system.dto.request.MailBody;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Slf4j
@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final BookingRepository bookingRepository;

    public EmailService(JavaMailSender mailSender, TemplateEngine templateEngine, BookingRepository bookingRepository) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
        this.bookingRepository = bookingRepository;
    }

    public void sendWelcomeEmail(String toEmail) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("bambospa.skincare@gmail.com");
            message.setTo(toEmail);
            message.setSubject("Chào mừng bạn đến với Bamboo Spa!");
            message.setText("Cảm ơn bạn đã đăng ký tài khoản tại Bamboo Spa. Chúc bạn có trải nghiệm tuyệt vời!");

            mailSender.send(message);
            log.info("Email sent successfully to {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", toEmail, e.getMessage());
        }
    }

    public void sendSimpleMessage(MailBody mailBody) {
        try {
            Context context = new Context();
            context.setVariable("name", mailBody.getTo());
            context.setVariable("otp", mailBody.getOtp());
            String template = templateEngine.process("OTP-ForgotPassword", context);
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
            mimeMessageHelper.setFrom("bambospa.skincare@gmail.com");
            mimeMessageHelper.setTo(mailBody.getTo());
            mimeMessageHelper.setText(template, true);
            mimeMessageHelper.setSubject(mailBody.getSubject());
            mailSender.send(mimeMessage);
        } catch (MessagingException exception) {
            System.out.println("Can't not send email");
        }
    }

    public void sendMailChangeTherapist(ChangeTherapist request) {
        try {
            Context context = new Context();
            context.setVariable("name", request.getTo());
            context.setVariable("date", request.getDate());
            context.setVariable("therapistName", request.getTherapistName());
            context.setVariable("time", request.getTime());
            String template = templateEngine.process("ChangeTherapist", context);
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
            mimeMessageHelper.setFrom("bambospa.skincare@gmail.com");
            mimeMessageHelper.setTo(request.getTo());
            mimeMessageHelper.setText(template, true);
            mimeMessageHelper.setSubject(request.getSubject());
            mailSender.send(mimeMessage);
        } catch (MessagingException exception) {
            System.out.println("Can't not send email");
        }
    }

    public void sendMailInformBookingSuccess(CreateNewBookingSuccess createNewBookingSuccess) {
        try {
            Context context = new Context();
            context.setVariable("name", createNewBookingSuccess.getTo());
            context.setVariable("date", createNewBookingSuccess.getDate());
            context.setVariable("therapistName", createNewBookingSuccess.getTherapistName());
            context.setVariable("time", createNewBookingSuccess.getTime());
            String template = templateEngine.process("CreateNewBooking", context);
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
            mimeMessageHelper.setFrom("bambospa.skincare@gmail.com");
            mimeMessageHelper.setTo(createNewBookingSuccess.getTo());
            mimeMessageHelper.setText(template, true);
            mimeMessageHelper.setSubject(createNewBookingSuccess.getSubject());
            mailSender.send(mimeMessage);
        } catch (MessagingException exception) {
            System.out.println("Can't not send email");
        }
    }

    public void sendReminderMail(ReminderBooking reminderBooking){
        try {
            Context context = new Context();
            context.setVariable("name",reminderBooking.getTo());
            context.setVariable("date",reminderBooking.getDate());
            context.setVariable("stylistName",reminderBooking.getTherapistName());
            context.setVariable("time",reminderBooking.getTime());
            String template = templateEngine.process("ReminderBooking",context);
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
            mimeMessageHelper.setFrom("bambospa.skincare@gmail.com");
            mimeMessageHelper.setTo(reminderBooking.getTo());
            mimeMessageHelper.setText(template,true);
            mimeMessageHelper.setSubject(reminderBooking.getSubject());
            mailSender.send(mimeMessage);
        }catch (MessagingException exception){
            System.out.println("Can't not send email");

        }
    }

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void sendAutomatic(){
        System.out.println("hello");
        LocalDate date = LocalDate.now();
        List<Booking> bookings = bookingRepository.getBookingByDateAndStatusPending(date);
        LocalTime now = LocalTime.now();
        for(Booking booking : bookings){
            LocalTime newTime = booking.getSlot().getSlottime().minusMinutes(15);
            if((newTime.getHour() == now.getHour()) && (newTime.getMinute() == now.getMinute())){
                ReminderBooking reminderBooking = ReminderBooking.builder()
                        .to(booking.getUser().getEmail())
                        .subject("Reminder Your Booking")
                        .therapistName(booking.getTherapistSchedule().getTherapist().getFullName())
                        .date(booking.getBookingDay())
                        .time(booking.getSlot().getSlottime())
                        .build();
                sendReminderMail(reminderBooking);
                System.out.println("Send mail success");
            }
            if(now.isAfter(booking.getSlot().getSlottime().plusMinutes(20)) && booking.getStatus().equals(BookingStatus.PENDING)){
                booking.setStatus(BookingStatus.CANCELLED);
                bookingRepository.save(booking);
            }
        }
    }
}
