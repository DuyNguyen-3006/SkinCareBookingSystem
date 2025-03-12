package com.skincare_booking_system.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;

import com.skincare_booking_system.dto.request.ApiResponse;
import com.skincare_booking_system.dto.request.BlogRequest;
import com.skincare_booking_system.dto.request.BlogUpdateRequest;
import com.skincare_booking_system.dto.response.BlogResponse;
import com.skincare_booking_system.service.BlogService;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/blogs")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class BlogController {
    BlogService blogService;

    @PostMapping
    ApiResponse<BlogResponse> createRequest(@RequestBody @Valid BlogRequest request) {
        ApiResponse<BlogResponse> response = new ApiResponse<>();
        response.setResult(blogService.createBlog(request));
        response.setSuccess(true);
        return response;
    }

    @GetMapping
    ApiResponse<List<BlogResponse>> getAllBlogs() {
        return ApiResponse.<List<BlogResponse>>builder()
                .result(blogService.getAllBlogs())
                .build();
    }

    @GetMapping("/publish")
    ApiResponse<List<BlogResponse>> getAllBlogsTrue() {
        return ApiResponse.<List<BlogResponse>>builder()
                .result(blogService.getAllBlogsIsActiveTrue())
                .build();
    }

    @GetMapping("/unpublish")
    ApiResponse<List<BlogResponse>> getAllBlogsFalse() {
        return ApiResponse.<List<BlogResponse>>builder()
                .result(blogService.getAllBlogsIsActiveFalse())
                .build();
    }

    @GetMapping("/searchByTitleCUS")
    ApiResponse<List<BlogResponse>> getBlogByTitleCUS(@RequestParam String title) {
        return ApiResponse.<List<BlogResponse>>builder()
                .result(blogService.getBlogByTitleCUS(title))
                .build();
    }

    @GetMapping("/searchByTitle")
    ApiResponse<List<BlogResponse>> getBlogByTitle(@RequestParam String title) {
        return ApiResponse.<List<BlogResponse>>builder()
                .result(blogService.getBlogByTitle(title))
                .build();
    }

    @PutMapping("/update/{title}")
    ApiResponse<BlogResponse> updateBlog(
            @PathVariable String title, @Valid @RequestBody BlogUpdateRequest blogUpdateRequest) {
        return ApiResponse.<BlogResponse>builder()
                .result(blogService.updateBlog(title, blogUpdateRequest))
                .build();
    }

    @PutMapping("/publish/{title}")
    ApiResponse<String> publishBlog(@PathVariable String title) {
        return ApiResponse.<String>builder()
                .result(blogService.publishBlog(title))
                .build();
    }

    @PutMapping("/unpublish/{title}")
    ApiResponse<String> unpublishBlog(@PathVariable String title) {
        return ApiResponse.<String>builder()
                .result(blogService.unpublishBlog(title))
                .build();
    }
}
