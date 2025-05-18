package khuong.com.tmbackend.user_service.service.impl;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import khuong.com.tmbackend.user_service.dto.UpdateUserProfileRequest;
import khuong.com.tmbackend.user_service.dto.UserProfileDTO;
import khuong.com.tmbackend.user_service.entity.User;
import khuong.com.tmbackend.user_service.repository.UserRepository;
import khuong.com.tmbackend.user_service.service.UserProfileService;

@Service
public class UserProfileServiceImpl implements UserProfileService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserProfileDTO getCurrentUserProfile() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return convertToDTO(user);
    }

    @Override
    @Transactional
    public UserProfileDTO updateCurrentUserProfile(UpdateUserProfileRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Update user fields if they are not null in the request
        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }
        if (request.getEmail() != null) {
            // Check if email is already taken by another user
            if (!request.getEmail().equals(user.getEmail()) && 
                userRepository.existsByEmail(request.getEmail())) {
                throw new RuntimeException("Email đã được sử dụng bởi người dùng khác");
            }
            user.setEmail(request.getEmail());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        if (request.getAddress() != null) {
            user.setAddress(request.getAddress());
        }
        if (request.getGender() != null) {
            user.setGender(request.getGender());
        }

        // Save the updated user
        user = userRepository.save(user);
        return convertToDTO(user);
    }

    private UserProfileDTO convertToDTO(User user) {
        UserProfileDTO profileDTO = new UserProfileDTO();
        profileDTO.setId(user.getId());
        profileDTO.setUsername(user.getUsername());
        profileDTO.setEmail(user.getEmail());
        profileDTO.setFullName(user.getFullName());
        profileDTO.setPhone(user.getPhone());
        profileDTO.setAddress(user.getAddress());
        profileDTO.setGender(user.getGender());
        
        // Convert Set<UserRole> to List<String>
        List<String> roleNames = user.getRoles().stream()
                .map(userRole -> userRole.getRole().getName().name())
                .collect(Collectors.toList());
        profileDTO.setRoles(roleNames);
        
        profileDTO.setEnabled(user.isEnabled());
        
        // Convert Instant to LocalDateTime
        profileDTO.setCreatedAt(LocalDateTime.ofInstant(user.getCreatedAt(), ZoneId.systemDefault()));
        profileDTO.setLastLogin(LocalDateTime.ofInstant(user.getUpdatedAt(), ZoneId.systemDefault())); // Using updatedAt as lastLogin
        
        // TODO: Implement these fields when purchase service is available
        profileDTO.setOrderCount(0);
        profileDTO.setTotalSpent(0.0);
        profileDTO.setCartItems(0);
        profileDTO.setWishlistItems(0);

        return profileDTO;
    }
} 