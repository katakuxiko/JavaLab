package com.example.springrest.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Autowired
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendSimpleMessage(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("katakuxiko@gmail.com"); // Замените на ваш email
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }

    public void sendHtmlMessage(String to, String subject, String htmlContent) {
        MimeMessage message = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");
            helper.setFrom("katakuxiko@gmail.com"); // Замените на ваш email
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // true = HTML
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Не удалось отправить HTML-письмо", e);
        }
    }

    public void sendMessageWithAttachment(String to, String subject, String text, MultipartFile file) {
        MimeMessage message = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom("katakuxiko@gmail.com"); // Замените на ваш email
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text);

            helper.addAttachment(file.getOriginalFilename(), file::getInputStream);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Не удалось отправить письмо с вложением", e);
        }
    }


    public void sendMessageWithBase64Attachment(String to, String subject, String text, String fileName, String fileBase64) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text);

            byte[] fileBytes = Base64.getDecoder().decode(fileBase64);
            ByteArrayResource fileResource = new ByteArrayResource(fileBytes);

            helper.addAttachment(fileName, fileResource);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Ошибка при отправке письма: " + e.getMessage(), e);
        }
    }

    public void sendMassMessage(List<String> to, String subject, String text) {
        for (String recipient : to) {
            sendSimpleMessage(recipient, subject, text);  // Мы используем sendSimpleMessage для массовой отправки
        }
    }

    public void sendEnrollmentNotification(String to, String studentName, String courseTitle, LocalDate enrollmentDate) {
        String subject = "Enrollment Confirmation";
        String text = String.format(
                "Dear %s,\n\nYou have been successfully enrolled in the course \"%s\" starting from %s.\n\n Good learning",
                studentName, courseTitle, enrollmentDate
        );
        sendSimpleMessage(to, subject, text);
    }

}
