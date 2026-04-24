package com.billbharat.sales.controller;

import com.billbharat.sales.dto.request.LoginRequest;
import com.billbharat.sales.dto.request.RefreshTokenRequest;
import com.billbharat.sales.dto.request.RegisterRequest;
import com.billbharat.sales.dto.response.AuthResponse;
import com.billbharat.sales.service.AuthService;
import com.billbharat.sales.util.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/sales/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Auth endpoints")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Login with phone and password")
    public ResponseEntity<Map<String, Object>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ResponseUtil.success("Login successful", response));
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    public ResponseEntity<Map<String, Object>> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.ok(ResponseUtil.success("Registration successful", response));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token")
    public ResponseEntity<Map<String, Object>> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        AuthResponse response = authService.refreshToken(request);
        return ResponseEntity.ok(ResponseUtil.success("Token refreshed", response));
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout user")
    public ResponseEntity<Map<String, Object>> logout() {
        return ResponseEntity.ok(ResponseUtil.success("Logged out successfully", null));
    }

    @PostMapping("/verify-otp")
    @Operation(summary = "Verify OTP (placeholder)")
    public ResponseEntity<Map<String, Object>> verifyOtp(@RequestBody Map<String, String> body) {
        return ResponseEntity.ok(ResponseUtil.success("OTP verified", null));
    }
}
