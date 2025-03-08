package com.skincare_booking_system.mapper;


import com.skincare_booking_system.dto.request.BlogRequest;
import com.skincare_booking_system.dto.request.BlogUpdateRequest;
import com.skincare_booking_system.dto.response.BlogResponse;
import com.skincare_booking_system.entities.Blog;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface BlogMapper {
    Blog toBlog(BlogRequest blogRequest);

    void updateBlog(@MappingTarget Blog blog, BlogUpdateRequest blogUpdateRequest);

    BlogResponse toBlogResponse(Blog blog);
}
