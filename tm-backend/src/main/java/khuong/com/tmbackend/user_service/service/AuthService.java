package khuong.com.tmbackend.user_service.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import khuong.com.tmbackend.user_service.entity.ERole;
import khuong.com.tmbackend.user_service.entity.PasswordResetToken;
import khuong.com.tmbackend.user_service.entity.Role;
import khuong.com.tmbackend.user_service.entity.User;
import khuong.com.tmbackend.user_service.entity.UserRole;
import khuong.com.tmbackend.user_service.exception.ResourceNotFoundException;
import khuong.com.tmbackend.user_service.payload.request.ChangePasswordRequest;
import khuong.com.tmbackend.user_service.payload.request.ForgotPasswordRequest;
import khuong.com.tmbackend.user_service.payload.request.LoginRequest;
import khuong.com.tmbackend.user_service.payload.request.ResetPasswordRequest;
import khuong.com.tmbackend.user_service.payload.request.SignupRequest;
import khuong.com.tmbackend.user_service.payload.request.TokenRefreshRequest;
import khuong.com.tmbackend.user_service.payload.response.JwtResponse;
import khuong.com.tmbackend.user_service.payload.response.MessageResponse;
import khuong.com.tmbackend.user_service.repository.PasswordResetTokenRepository;
import khuong.com.tmbackend.user_service.repository.RoleRepository;
import khuong.com.tmbackend.user_service.repository.UserRepository;
import khuong.com.tmbackend.user_service.security.JwtUtils;
import khuong.com.tmbackend.user_service.security.UserDetailsImpl;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private PasswordEncoder encoder;
    
    @Autowired
    private JavaMailSender mailSender;

    @Transactional
    public ResponseEntity<?> registerUser(SignupRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
        }

        User user = new User(signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()));
        
        user.setFullName(signUpRequest.getFullName());
        user.setPhone(signUpRequest.getPhone());
        user.setAddress(signUpRequest.getAddress());
        user.setGender(signUpRequest.getGender());

        Set<String> strRoles = signUpRequest.getRoles();
        Set<UserRole> userRoles = new HashSet<>();

        if (strRoles == null || strRoles.isEmpty()) {
            Role userRole = roleRepository.findByName(ERole.ROLE_CUSTOMER)
                    .orElseThrow(() -> new RuntimeException("Error: Default role is not found."));
            userRoles.add(new UserRole(user, userRole));
        } else {
            strRoles.forEach(role -> {
                try {
                    ERole eRole = ERole.valueOf(role);
                    Role foundRole = roleRepository.findByName(eRole)
                            .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                    userRoles.add(new UserRole(user, foundRole));
                } catch (IllegalArgumentException e) {
                    throw new RuntimeException("Error: Role " + role + " is not found.");
                }
            });
        }

        user.setRoles(userRoles);
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }

    public ResponseEntity<?> login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
    
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);
    
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());
        
        // Add debug log
        System.out.println("User roles: " + roles);
    
        return ResponseEntity.ok(new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles));
    }

    public ResponseEntity<?> logout() {
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok(new MessageResponse("Logout successful"));
    }

    public ResponseEntity<?> refreshToken(TokenRefreshRequest request) {
        String token = request.getRefreshToken();
        if (jwtUtils.validateJwtToken(token)) {
            String newToken = jwtUtils.generateJwtTokenFromRefreshToken(token);
            String username = jwtUtils.getUserNameFromJwtToken(token);
            Long userId = jwtUtils.getUserIdFromJwtToken(token);
            
            return ResponseEntity.ok(new JwtResponse(
                newToken, 
                userId,
                username,
                null, 
                null));
        } else {
            return ResponseEntity.badRequest().body(new MessageResponse("Invalid refresh token"));
        }
    }

    @Transactional
    public ResponseEntity<?> forgotPassword(ForgotPasswordRequest request) {
        String email = request.getEmail();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        // Revoke any existing tokens
        tokenRepository.findByUser(user).ifPresent(tokenRepository::delete);
        
        // Create new token
        PasswordResetToken passwordResetToken = new PasswordResetToken(user);
        tokenRepository.save(passwordResetToken);
        
        // Send email with reset link
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(user.getEmail());
        mailMessage.setSubject("Password Reset Request");
        mailMessage.setText("To reset your password, click the link below:\n\n" + 
                "http://your-website.com/reset-password?token=" + passwordResetToken.getToken());
        
        mailSender.send(mailMessage);
        
        return ResponseEntity.ok(new MessageResponse("Password reset email has been sent to " + email));
    }

    @Transactional
    public ResponseEntity<?> resetPassword(ResetPasswordRequest request) {
        String token = request.getToken();
        String newPassword = request.getNewPassword();
        
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid token"));
        
        if (resetToken.isExpired()) {
            tokenRepository.delete(resetToken);
            return ResponseEntity.badRequest().body(new MessageResponse("Token has expired"));
        }
        
        User user = resetToken.getUser();
        user.setPassword(encoder.encode(newPassword));
        userRepository.save(user);
        
        // Delete the token after use
        tokenRepository.delete(resetToken);
        
        return ResponseEntity.ok(new MessageResponse("Password has been reset successfully"));
    }
    
    @Transactional
    public ResponseEntity<?> changePassword(ChangePasswordRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        // Verify current password
        if (!encoder.matches(request.getCurrentPassword(), user.getPassword())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Current password is incorrect"));
        }
        
        // Verify new password and confirmation match
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            return ResponseEntity.badRequest().body(new MessageResponse("New password and confirmation do not match"));
        }
        
        // Update password
        user.setPassword(encoder.encode(request.getNewPassword()));
        userRepository.save(user);
        
        return ResponseEntity.ok(new MessageResponse("Password changed successfully"));
    }
}