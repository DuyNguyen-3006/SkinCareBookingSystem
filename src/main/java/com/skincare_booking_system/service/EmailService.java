package com.skincare_booking_system.service;

import com.skincare_booking_system.dto.request.MailBody;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Slf4j
@Service
public class EmailService {

    private  final JavaMailSender mailSender;
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

    public void sendSimpleMessage(MailBody mailBody){
        try {
            Context context = new Context();
            context.setVariable("name",mailBody.getTo());
            context.setVariable("otp",mailBody.getOtp());
            String template = templateEngine.process("OTP-ForgotPassword",context);
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
            mimeMessageHelper.setFrom("fsalon391@gmail.com");
            mimeMessageHelper.setTo(mailBody.getTo());
            mimeMessageHelper.setText(template,true);
            mimeMessageHelper.setSubject(mailBody.getSubject());
            mailSender.send(mimeMessage);
        }catch (MessagingException exception){
            System.out.println("Can't not send email");
        }
    }
}
