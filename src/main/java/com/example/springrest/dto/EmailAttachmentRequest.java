package com.example.springrest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.web.multipart.MultipartFile;

public class EmailAttachmentRequest {

    @Schema(description = "Получатель", example = "test@mail.com")
    public String to;

    @Schema(description = "Тема письма", example = "Привет!")
    public String subject;

    @Schema(description = "Текст письма", example = "Это письмо с файлом.")
    public String text;

    @Schema(description = "Файл", type = "string", format = "binary")
    public MultipartFile file;
}
