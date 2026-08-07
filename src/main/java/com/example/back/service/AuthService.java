package com.example.back.service;

import com.example.back.dto.AuthRequest;
import com.example.back.dto.AuthResponse;
import com.example.back.dto.RegisterRequest;
import com.example.back.model.User;
import com.example.back.repository.UserRepository;
import com.example.back.security.JwtService;
import com.example.back.service.log.UserLogService;

import jakarta.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.Map;

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
    private final UserLogService userLogService;

    public AuthService(UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            UserDetailsService userDetailsService,
            UserLogService userLogService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.userLogService = userLogService;
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsById(request.getId())) {
            throw new IllegalArgumentException("Id already exists");
        }

        User user = new User(request.getId(), passwordEncoder.encode(request.getPassword()), request.getName(),
                "ROLE_USER");
        userRepository.save(user);

        // 회원가입 후 DB에서 권한 정보를 포함한 UserDetails 로드
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getId());
        String token = jwtService.generateToken(userDetails);
        return new AuthResponse(token);
    }

    public AuthResponse login(HttpServletRequest httpRequest, AuthRequest request) {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getId(), request.getPassword()));

            // 로그인 기록(인증 후)
            Map<String, Object> insertLogMap = new HashMap<String, Object>();

            String ipAddress = getClientIp(httpRequest);
            insertLogMap.put("logType", "LOGIN");
            insertLogMap.put("id", request.getId());
            insertLogMap.put("ip", ipAddress);

            userLogService.insertUserLog(insertLogMap);
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

    private String getClientIp(HttpServletRequest request) {
        // 1. 대부분의 프록시 서버(Nginx, AWS 등)가 진짜 IP를 담는 표준 헤더
        String ip = request.getHeader("X-Forwarded-For");

        // 2. WebLogic 등 특정 WAS나 구형 프록시가 사용하는 헤더들 순차 검사
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }

        // 3. 만약 위 헤더들이 전부 없다면 프록시가 없는 환경이므로 원래 장비 주소를 가져옴
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        // 💡 중요: X-Forwarded-For 헤더는 "사용자IP, 프록시1IP, 프록시2IP" 형태로 들어올 수 있습니다.
        // 맨 앞에 있는 주소가 "진짜 사용자 IP"이므로, 콤마(,)가 있다면 첫 번째 값만 잘라냅니다.
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }

        return ip;
    }
}
