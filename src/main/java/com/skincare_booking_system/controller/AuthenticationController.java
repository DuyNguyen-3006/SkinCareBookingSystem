package com.skincare_booking_system.controller;

import com.nimbusds.jose.JOSEException;
import com.skincare_booking_system.dto.request.ApiResponse;
import com.skincare_booking_system.dto.request.AuthenticationRequest;
import com.skincare_booking_system.dto.request.IntrospectRequest;
import com.skincare_booking_system.dto.response.AuthenticationResponse;
import com.skincare_booking_system.dto.response.IntrospectResponse;
import com.skincare_booking_system.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

@RestController
@RequestMapping("/authentication")
public class AuthenticationController {
    @Autowired
    private AuthenticationService authenticationService;


    @PostMapping("/log-in")
    ApiResponse<AuthenticationResponse> logIn(@RequestBody AuthenticationRequest request) {
        var result = authenticationService.login(request);

        return ApiResponse.<AuthenticationResponse>builder()
                .result(result)
                .build();
    }

    @PostMapping("/introspect")
    ApiResponse<IntrospectResponse> authenticate(@RequestBody IntrospectRequest request)
            throws JOSEException, ParseException {
        var result = authenticationService.introspect(request);

        return ApiResponse.<IntrospectResponse>builder()
                .result(result)
                .build();
    }
}
