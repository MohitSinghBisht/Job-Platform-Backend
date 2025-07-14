package com.jobhunt.security.services;

import com.jobhunt.payload.request.LoginRequest;
import com.jobhunt.payload.request.SignupRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

public interface UserService {

    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest);

    public ResponseEntity<?> registerUser(SignupRequest signUpRequest);
}
