package com.skincare_booking_system.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.skincare_booking_system.dto.request.ChangeTherapist;
import com.skincare_booking_system.dto.request.CreateNewBookingSuccess;
import com.skincare_booking_system.dto.request.MailBody;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private TemplateEngine templateEngine;

    public EmailService(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
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

    public void sendMailChangeStylist(ChangeTherapist request) {
        try {
            Context context = new Context();
            context.setVariable("name", request.getTo());
            context.setVariable("date", request.getDate());
            context.setVariable("therapistName", request.getTherapistName());
            context.setVariable("time", request.getTime());
            String template = templateEngine.process("ChangeStylist", context);
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
}
