package com.example.back.service;

import com.example.back.dto.AuthRequest;
import com.example.back.dto.AuthResponse;
import com.example.back.dto.RegisterRequest;
import com.example.back.model.User;
import com.example.back.repository.UserRepository;
import com.example.back.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtService jwtService,
                       UserDetailsService userDetailsService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsById(request.getId())) {
            throw new IllegalArgumentException("Id already exists");
        }

        User user = new User(request.getId(), passwordEncoder.encode(request.getPassword()), request.getName(), "ROLE_USER");
        userRepository.save(user);

        // 회원가입 후 DB에서 권한 정보를 포함한 UserDetails 로드
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getId());
        String token = jwtService.generateToken(userDetails);
        return new AuthResponse(token);
    }

    public AuthResponse login(AuthRequest request) {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getId(), request.getPassword())
            );
        } catch (AuthenticationException e) {
            throw new IllegalArgumentException("Invalid username or password");
        }

        // 인증된 결과(Principal)에서 실제 권한이 포함된 UserDetails 추출
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtService.generateToken(userDetails);
        return new AuthResponse(token);
    }

    public AuthResponse refreshToken(String token) {
        String userId = jwtService.extractUsername(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(userId);
        
        if (jwtService.isTokenValid(token, userDetails)) {
            String newToken = jwtService.generateToken(userDetails);
            return new AuthResponse(newToken);
        }
        throw new IllegalArgumentException("Invalid or expired token");
    }
}
