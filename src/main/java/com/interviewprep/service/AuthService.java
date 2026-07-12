package com.interviewprep.service;

import com.interviewprep.dto.request.LoginRequest;
import com.interviewprep.dto.request.RegisterRequest;
import com.interviewprep.dto.response.AuthResponse;
import com.interviewprep.entity.User;
import com.interviewprep.exception.ResourceAlreadyExistsException;
import com.interviewprep.repository.UserRepository;
import com.interviewprep.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ResourceAlreadyExistsException("Username already taken: " + request.getUsername());
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResourceAlreadyExistsException("Email already registered: " + request.getEmail());
        }

        User user = User.builder()
            .fullName(request.getFullName())
            .username(request.getUsername())
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .college(request.getCollege())
            .graduationYear(request.getGraduationYear())
            .targetRole(request.getTargetRole())
            .role(User.Role.STUDENT)
            .lastActive(LocalDate.now())
            .build();

        user = userRepository.save(user);
        String token = jwtUtil.generateToken(user);

        log.info("New user registered: {}", user.getUsername());

        return AuthResponse.builder()
            .token(token)
            .userId(user.getId())
            .username(user.getUsername())
            .email(user.getEmail())
            .fullName(user.getFullName())
            .role(user.getRole().name())
            .message("Registration successful! Welcome to AI Interview Prep.")
            .build();
    }

    public AuthResponse login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.getUsernameOrEmail(),
                    request.getPassword()
                )
            );

            User user = (User) authentication.getPrincipal();
            user.setLastActive(LocalDate.now());
            userRepository.save(user);

            String token = jwtUtil.generateToken(user);
            log.info("User logged in: {}", user.getUsername());

            return AuthResponse.builder()
                .token(token)
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole().name())
                .message("Login successful!")
                .build();

        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Invalid username/email or password");
        } catch (DisabledException e) {
            throw new DisabledException("Account is disabled. Please contact support.");
        }
    }
}
