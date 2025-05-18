package khuong.com.tmbackend.product_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import khuong.com.tmbackend.product_service.payload.PagedResponse;
import khuong.com.tmbackend.product_service.payload.ReviewRequestDTO;
import khuong.com.tmbackend.product_service.payload.ReviewResponseDTO;
import khuong.com.tmbackend.product_service.service.ReviewService;
import khuong.com.tmbackend.user_service.repository.UserRepository;

@RestController
@RequestMapping("/api")
public class ReviewController {
    
    @Autowired
    private ReviewService reviewService;
    
    @Autowired
    private UserRepository userRepository;
    
    @GetMapping("/products/{productId}/reviews")
    public ResponseEntity<PagedResponse<ReviewResponseDTO>> getProductReviews(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        PagedResponse<ReviewResponseDTO> reviews = reviewService.getProductReviews(productId, page, size);
        return ResponseEntity.ok(reviews);
    }
    
    @PostMapping("/products/{productId}/reviews")
    public ResponseEntity<ReviewResponseDTO> createReview(
            @PathVariable Long productId,
            @Valid @RequestBody ReviewRequestDTO reviewRequest,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        // Get user ID from authenticated user
        Long userId = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"))
                .getId();
        
        // Set product ID in the request
        reviewRequest.setProductId(productId);
        
        ReviewResponseDTO newReview = reviewService.createReview(reviewRequest, userId);
        return new ResponseEntity<>(newReview, HttpStatus.CREATED);
    }
    
    @PutMapping("/products/{productId}/reviews/{reviewId}")
    public ResponseEntity<ReviewResponseDTO> updateReview(
            @PathVariable Long productId,
            @PathVariable Long reviewId,
            @Valid @RequestBody ReviewRequestDTO reviewRequest,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        // Get user ID from authenticated user
        Long userId = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"))
                .getId();
        
        // Set product ID in the request
        reviewRequest.setProductId(productId);
        
        ReviewResponseDTO updatedReview = reviewService.updateReview(reviewId, reviewRequest, userId);
        return ResponseEntity.ok(updatedReview);
    }
    
    @DeleteMapping("/products/{productId}/reviews/{reviewId}")
    public ResponseEntity<Void> deleteReview(
            @PathVariable Long productId,
            @PathVariable Long reviewId,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        // Get user ID from authenticated user
        Long userId = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"))
                .getId();
        
        reviewService.deleteReview(reviewId, userId);
        return ResponseEntity.noContent().build();
    }
} 