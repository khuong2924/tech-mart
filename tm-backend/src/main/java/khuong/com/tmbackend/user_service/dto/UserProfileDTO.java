package khuong.com.tmbackend.user_service.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
public class UserProfileDTO {
    private Long id;
    private String username;
    private String email;
    private String fullName;
    private String phone;
    private String address;
    private String gender;
    private List<String> roles;
    private boolean enabled;
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;
    private int orderCount;
    private double totalSpent;
    private int cartItems;
    private int wishlistItems;
} 