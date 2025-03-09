package com.skincare_booking_system.dto.request;


import com.skincare_booking_system.validator.ImageURL;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class BlogRequest {
    @NotBlank(message = "TITLE_NOT_EMPTY")
    @Size(min = 10, max = 200, message = "TITLE_INVALID")
    String title;
    @NotBlank(message = "CONTENT_NOT_EMPTY")
    @Size(min = 100, message = "CONTEXT_INVALID")
    String content;
    @NotBlank(message = "IMG_URL_REQUIRED")
    @Size(max = 500, message = "IMG_URL_TOO_LONG")
    @ImageURL(message = "IMG_URL_INVALID")
    String imageUrl;
    boolean active;
}
