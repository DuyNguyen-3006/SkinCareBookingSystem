package com.skincare_booking_system.service;


import com.skincare_booking_system.dto.request.BlogRequest;
import com.skincare_booking_system.dto.request.BlogUpdateRequest;
import com.skincare_booking_system.dto.response.BlogResponse;
import com.skincare_booking_system.entities.Blog;
import com.skincare_booking_system.exception.AppException;
import com.skincare_booking_system.exception.ErrorCode;
import com.skincare_booking_system.mapper.BlogMapper;
import com.skincare_booking_system.repository.BlogRepository;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class BlogService {
    BlogMapper blogMapper;
    BlogRepository blogRepository;
    @PreAuthorize("hasRole('ADMIN')")
    public BlogResponse createBlog(BlogRequest blogRequest) {
        if(blogRepository.existsBlogByTitle(blogRequest.getTitle())){
            throw new AppException(ErrorCode.BLOG_ALREADY_USED);
        }
        Blog blog = blogMapper.toBlog(blogRequest);
        blog.setActive(false);
        return blogMapper.toBlogResponse(blogRepository.save(blog));
    }
    public List<BlogResponse> getBlogByTitleCUS(String  title) {
        List<Blog> blogs = blogRepository.findByTitleContainingIgnoreCaseAndActiveTrue(title);
        if(blogs.isEmpty()){
            throw new AppException(ErrorCode.BLOG_NOT_FOUND);
        }
        return blogs.stream().map(blogMapper::toBlogResponse).collect(Collectors.toList());
    }
    @PreAuthorize("hasRole('ADMIN')")
    public List<BlogResponse> getBlogByTitle(String  title) {
        List<Blog> blogs = blogRepository.findByTitleContainingIgnoreCase(title);
        if(blogs.isEmpty()){
            throw new AppException(ErrorCode.BLOG_NOT_FOUND);
        }
        return blogs.stream().map(blogMapper::toBlogResponse).collect(Collectors.toList());
    }
    @PreAuthorize("hasRole('ADMIN')")
    public List<BlogResponse> getAllBlogs() {
        List<Blog> blogs = blogRepository.findAll();
        if(blogs.isEmpty()){
            throw new AppException(ErrorCode.BLOG_NOT_FOUND);
        }
        return blogs.stream().map(blogMapper::toBlogResponse).collect(Collectors.toList());
    }
    public List<BlogResponse> getAllBlogsIsActiveTrue() {
        List<Blog> publishBlogs =blogRepository.findByActiveTrue();
        if(publishBlogs.isEmpty()){
            throw new  AppException(ErrorCode.BLOG_NOT_FOUND);
        }
        return publishBlogs.stream()
                .map(blogMapper::toBlogResponse)
                .toList();
    }
    @PreAuthorize("hasRole('ADMIN')")
    public List<BlogResponse> getAllBlogsIsActiveFalse() {
        List<Blog> publishBlogs =blogRepository.findByActiveFalse();
        if(publishBlogs.isEmpty()){
            throw new  AppException(ErrorCode.BLOG_NOT_FOUND);
        }
        return publishBlogs.stream()
                .map(blogMapper::toBlogResponse)
                .toList();
    }
    @PreAuthorize("hasRole('ADMIN')")
    public String publishBlog(String title) {
        Blog blog = blogRepository.findBlogByTitle(title).orElseThrow(() -> new AppException(ErrorCode.BLOG_NOT_FOUND));
        blog.setActive(true);
        blogRepository.save(blog);
        return "Blog publish successfully";
    }
    @PreAuthorize("hasRole('ADMIN')")
    public String unpublishBlog(String title) {
        Blog blog = blogRepository.findBlogByTitle(title).orElseThrow(() -> new AppException(ErrorCode.BLOG_NOT_FOUND));
        blog.setActive(false);
        blogRepository.save(blog);
        return "Blog unpublish successfully";
    }
    @PreAuthorize("hasRole('ADMIN')")
    public BlogResponse updateBlog(String title, BlogUpdateRequest blogUpdateRequest) {
        Blog b = blogRepository.findBlogByTitle(title).orElseThrow(() -> new AppException(ErrorCode.BLOG_NOT_FOUND));
        blogMapper.updateBlog(b, blogUpdateRequest);
        return blogMapper.toBlogResponse(b);
    }
}
