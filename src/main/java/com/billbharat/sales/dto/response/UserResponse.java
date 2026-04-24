package com.billbharat.sales.dto.response;

import com.billbharat.sales.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private String id;
    private String name;
    private String phoneNumber;
    private String email;
    private String role;
    private String employeeId;
    private String profileImageUrl;
    private boolean isActive;
    private LocalDateTime createdAt;

    public static UserResponse fromEntity(User user) {
        return UserResponse.builder()
                .id(user.getId().toString())
                .name(user.getName())
                .phoneNumber(user.getPhoneNumber())
                .email(user.getEmail())
                .role(user.getRole().name())
                .employeeId(user.getEmployeeId())
                .profileImageUrl(user.getProfileImageUrl())
                .isActive(user.isActive())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
