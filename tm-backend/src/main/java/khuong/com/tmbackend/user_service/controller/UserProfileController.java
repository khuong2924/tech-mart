package khuong.com.tmbackend.user_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import khuong.com.tmbackend.user_service.dto.UpdateUserProfileRequest;
import khuong.com.tmbackend.user_service.dto.UserProfileDTO;
import khuong.com.tmbackend.user_service.service.UserProfileService;

@RestController
@RequestMapping("/api/users")
public class UserProfileController {

    @Autowired
    private UserProfileService userProfileService;

    @GetMapping("/profile")
    public ResponseEntity<UserProfileDTO> getCurrentUserProfile() {
        return ResponseEntity.ok(userProfileService.getCurrentUserProfile());
    }

    @PutMapping("/profile")
    public ResponseEntity<UserProfileDTO> updateCurrentUserProfile(
            @Valid @RequestBody UpdateUserProfileRequest request) {
        return ResponseEntity.ok(userProfileService.updateCurrentUserProfile(request));
    }
} 