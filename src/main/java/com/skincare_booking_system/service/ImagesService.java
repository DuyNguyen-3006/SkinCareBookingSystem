package com.skincare_booking_system.service;


import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


public interface ImagesService {
    public String uploadImage(MultipartFile file)throws IOException;
}
