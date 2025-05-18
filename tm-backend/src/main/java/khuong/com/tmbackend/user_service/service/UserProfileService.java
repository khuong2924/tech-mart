package khuong.com.tmbackend.user_service.service;

import khuong.com.tmbackend.user_service.dto.UpdateUserProfileRequest;
import khuong.com.tmbackend.user_service.dto.UserProfileDTO;

public interface UserProfileService {
    UserProfileDTO getCurrentUserProfile();
    UserProfileDTO updateCurrentUserProfile(UpdateUserProfileRequest request);
} 