package com.skincare_booking_system.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.skincare_booking_system.entities.Blog;

@Repository
public interface BlogRepository extends JpaRepository<Blog, String> {

    boolean existsBlogByTitle(String title);

    Optional<Blog> findBlogByTitle(String title);

    List<Blog> findByActiveTrue();

    List<Blog> findByActiveFalse();

    List<Blog> findByTitleContainingIgnoreCase(String title);

    List<Blog> findByTitleContainingIgnoreCaseAndActiveTrue(String title);
}
