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
@RequestMapping("/api/reviews")
public class ReviewController {
    
    @Autowired
    private ReviewService reviewService;
    
    @Autowired
    private UserRepository userRepository;
    
    @GetMapping("/product/{productId}")
    public ResponseEntity<PagedResponse<ReviewResponseDTO>> getProductReviews(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        PagedResponse<ReviewResponseDTO> reviews = reviewService.getProductReviews(productId, page, size);
        return ResponseEntity.ok(reviews);
    }
    
    @PostMapping
    public ResponseEntity<ReviewResponseDTO> createReview(
            @Valid @RequestBody ReviewRequestDTO reviewRequest,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        // Get user ID from authenticated user
        Long userId = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"))
                .getId();
        
        ReviewResponseDTO newReview = reviewService.createReview(reviewRequest, userId);
        return new ResponseEntity<>(newReview, HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ReviewResponseDTO> updateReview(
            @PathVariable Long id,
            @Valid @RequestBody ReviewRequestDTO reviewRequest,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        // Get user ID from authenticated user
        Long userId = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"))
                .getId();
        
        ReviewResponseDTO updatedReview = reviewService.updateReview(id, reviewRequest, userId);
        return ResponseEntity.ok(updatedReview);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        // Get user ID from authenticated user
        Long userId = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"))
                .getId();
        
        reviewService.deleteReview(id, userId);
        return ResponseEntity.noContent().build();
    }
} 