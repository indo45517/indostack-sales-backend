package com.billbharat.sales.service;

import com.billbharat.sales.dto.request.LoginRequest;
import com.billbharat.sales.dto.request.RefreshTokenRequest;
import com.billbharat.sales.dto.request.RegisterRequest;
import com.billbharat.sales.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse login(LoginRequest request);
    AuthResponse register(RegisterRequest request);
    AuthResponse refreshToken(RefreshTokenRequest request);
}
