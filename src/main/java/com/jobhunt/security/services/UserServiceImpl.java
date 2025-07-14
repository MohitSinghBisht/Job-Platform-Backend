package com.jobhunt.security.services;

import com.jobhunt.model.Role;
import com.jobhunt.model.User;
import com.jobhunt.payload.request.LoginRequest;
import com.jobhunt.payload.request.SignupRequest;
import com.jobhunt.payload.response.JwtResponse;
import com.jobhunt.payload.response.MessageResponse;
import com.jobhunt.repository.UserRepository;
import com.jobhunt.security.jwt.JwtUtils;
import com.jobhunt.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ProfileService profileService;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @Override
    public ResponseEntity<?> authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        User userDetails = (User) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        return ResponseEntity.ok(new JwtResponse(
                jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles));
    }

    public ResponseEntity<?> registerUser(SignupRequest signUpRequest) {

        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Username is already taken!"));
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        }

        // Create new user's account
        User user = new User();
        user.setUsername(signUpRequest.getUsername());
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(encoder.encode(signUpRequest.getPassword()));
        user.setFirstName(signUpRequest.getFirstName());
        user.setLastName(signUpRequest.getLastName());

        Set<Role> roles = new HashSet<>();
        if (signUpRequest.getRole().equalsIgnoreCase("recruiter")) {
            roles.add(Role.ROLE_RECRUITER);
        } else {
            roles.add(Role.ROLE_JOB_SEEKER);
        }

        user.setRoles(roles);
        User savedUser = userRepository.save(user);

        // Create profile based on role
        if (roles.contains(Role.ROLE_RECRUITER)) {
            profileService.createRecruiterProfile(savedUser);
        } else {
            profileService.createJobSeekerProfile(savedUser);
        }

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }




}
