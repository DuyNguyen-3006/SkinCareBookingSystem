package com.skincare_booking_system.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import com.skincare_booking_system.validator.ImageURL;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class BlogUpdateRequest {
    @NotBlank(message = "TITLE_NOT_EMPTY")
    @Size(min = 10, max = 200, message = "TITLE_INVALID")
    String title;

    @NotBlank(message = "CONTENT_NOT_EMPTY")
    @Size(min = 100, message = "CONTEXT_INVALID")
    String content;

    MultipartFile imgUrl;
}
