package com.example.back.service;

import com.example.back.dto.AuthRequest;
import com.example.back.dto.AuthResponse;
import com.example.back.dto.RegisterRequest;
import com.example.back.model.User;
import com.example.back.repository.UserRepository;
import com.example.back.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsById(request.getId())) {
            throw new IllegalArgumentException("Id already exists");
        }

        User user = new User(request.getId(), passwordEncoder.encode(request.getPassword()), request.getName());
        userRepository.save(user);

        // After registration, return a token
        var userDetails = org.springframework.security.core.userdetails.User
                .withUsername(user.getId())
                .password(user.getPassword())
                .build();
        String token = jwtService.generateToken(userDetails);
        return new AuthResponse(token);
    }

    public AuthResponse login(AuthRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getId(), request.getPassword())
            );
        } catch (AuthenticationException e) {
            throw new IllegalArgumentException("Invalid username or password");
        }

        var userDetails = org.springframework.security.core.userdetails.User
                .withUsername(request.getId())
                .password(request.getPassword())
                .build();

        String token = jwtService.generateToken(userDetails);
        return new AuthResponse(token);
    }
}
