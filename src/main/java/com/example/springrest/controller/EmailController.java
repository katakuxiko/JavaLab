package com.example.springrest.controller;

import com.example.springrest.dto.EmailAttachmentRequest;
import com.example.springrest.service.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import io.swagger.v3.oas.annotations.parameters.RequestBody; // ← ОК оставляем только здесь

import java.util.List;

@RestController
@RequestMapping("/api/email")
public class EmailController {

    private final EmailService emailService;

    @Autowired
    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/send")
    public String sendEmail(
            @RequestParam String to,
            @RequestParam String subject,
            @RequestParam String text) {
        emailService.sendSimpleMessage(to, subject, text);
        return "Письмо отправлено!";
    }

    @PostMapping("/send-html")
    public String sendHtmlEmail(
            @RequestParam String to,
            @RequestParam String subject,
            @RequestParam String html) {
        emailService.sendHtmlMessage(to, subject, html);
        return "HTML-письмо отправлено!";
    }

    @PostMapping(value = "/send-with-attachment", consumes = "multipart/form-data")
    public String sendEmailWithAttachment(
            @RequestParam String to,
            @RequestParam String subject,
            @RequestParam String text,
            @RequestParam("file") MultipartFile file) {
        emailService.sendMessageWithAttachment(to, subject, text, file);
        return "Письмо с вложением отправлено!";
    }


    @PostMapping("/send-mass")
    @Operation(
            summary = "Массовая отправка писем",

            description = "Отправка одного и того же письма нескольким получателям"
    )
    public String sendMassEmail(
            @RequestParam List<String> to,
            @RequestParam String subject,
            @RequestParam String text) {
        emailService.sendMassMessage(to, subject, text);
        return "Массовая отправка выполнена!";
    }
}
