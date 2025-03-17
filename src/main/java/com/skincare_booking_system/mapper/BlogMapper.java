package com.skincare_booking_system.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import com.skincare_booking_system.dto.request.BlogRequest;
import com.skincare_booking_system.dto.request.BlogUpdateRequest;
import com.skincare_booking_system.dto.response.BlogResponse;
import com.skincare_booking_system.entities.Blog;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface BlogMapper {
    Blog toBlog(BlogRequest blogRequest);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateBlog(@MappingTarget Blog blog, BlogUpdateRequest blogUpdateRequest);

    BlogResponse toBlogResponse(Blog blog);
}
