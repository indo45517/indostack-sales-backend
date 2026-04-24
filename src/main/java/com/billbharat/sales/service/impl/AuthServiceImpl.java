package com.billbharat.sales.service.impl;

import com.billbharat.sales.dto.request.LoginRequest;
import com.billbharat.sales.dto.request.RefreshTokenRequest;
import com.billbharat.sales.dto.request.RegisterRequest;
import com.billbharat.sales.dto.response.AuthResponse;
import com.billbharat.sales.dto.response.UserResponse;
import com.billbharat.sales.entity.User;
import com.billbharat.sales.exception.BadRequestException;
import com.billbharat.sales.repository.UserRepository;
import com.billbharat.sales.security.JwtTokenProvider;
import com.billbharat.sales.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getPhoneNumber(), request.getPassword())
        );

        String accessToken = jwtTokenProvider.generateToken(authentication);
        String refreshToken = jwtTokenProvider.generateRefreshToken(request.getPhoneNumber());

        User user = userRepository.findByPhoneNumber(request.getPhoneNumber())
                .orElseThrow(() -> new BadRequestException("User not found"));

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtTokenProvider.getJwtExpiration())
                .user(UserResponse.fromEntity(user))
                .build();
    }

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new BadRequestException("Phone number already registered");
        }

        User user = User.builder()
                .name(request.getName())
                .phoneNumber(request.getPhoneNumber())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .employeeId(request.getEmployeeId())
                .email(request.getEmail())
                .build();

        User savedUser = userRepository.save(user);

        String accessToken = jwtTokenProvider.generateToken(savedUser.getPhoneNumber());
        String refreshToken = jwtTokenProvider.generateRefreshToken(savedUser.getPhoneNumber());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtTokenProvider.getJwtExpiration())
                .user(UserResponse.fromEntity(savedUser))
                .build();
    }

    @Override
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        if (!jwtTokenProvider.validateToken(request.getRefreshToken())) {
            throw new BadRequestException("Invalid or expired refresh token");
        }

        String username = jwtTokenProvider.getUsernameFromToken(request.getRefreshToken());
        User user = userRepository.findByPhoneNumber(username)
                .orElseThrow(() -> new BadRequestException("User not found"));

        String accessToken = jwtTokenProvider.generateToken(username);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(username);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(newRefreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtTokenProvider.getJwtExpiration())
                .user(UserResponse.fromEntity(user))
                .build();
    }
}
