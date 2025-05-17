package khuong.com.tmbackend.product_service.service;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import khuong.com.tmbackend.product_service.entity.Product;
import khuong.com.tmbackend.product_service.entity.Review;
import khuong.com.tmbackend.product_service.exception.ResourceNotFoundException;
import khuong.com.tmbackend.product_service.payload.PagedResponse;
import khuong.com.tmbackend.product_service.payload.ReviewRequestDTO;
import khuong.com.tmbackend.product_service.payload.ReviewResponseDTO;
import khuong.com.tmbackend.product_service.repository.ProductRepository;
import khuong.com.tmbackend.product_service.repository.ReviewRepository;
import khuong.com.tmbackend.purchase_service.entity.OrderItem;
import khuong.com.tmbackend.purchase_service.repository.OrderItemRepository;
import khuong.com.tmbackend.user_service.entity.User;
import khuong.com.tmbackend.user_service.repository.UserRepository;

@Service
public class ReviewService {
    
    @Autowired
    private ReviewRepository reviewRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private OrderItemRepository orderItemRepository;
    
    public PagedResponse<ReviewResponseDTO> getProductReviews(Long productId, int page, int size) {
        // Validate product exists
        productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));
                
        // Create pageable with sorting by created date
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        
        // Get reviews
        Page<Review> reviews = reviewRepository.findByProductId(productId, pageable);
        
        return createReviewResponse(reviews);
    }
    
    @Transactional
    public ReviewResponseDTO createReview(ReviewRequestDTO reviewRequest, Long userId) {
        // Check if product exists
        Product product = productRepository.findById(reviewRequest.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + reviewRequest.getProductId()));
        
        // Check if user exists
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        // Check if user has already reviewed this product
        reviewRepository.findByProductIdAndUserId(product.getId(), user.getId())
                .ifPresent(existingReview -> {
                    throw new IllegalArgumentException("You have already reviewed this product");
                });
        
        // If orderItemId is provided, verify user purchased the product and mark item as reviewed
        if (reviewRequest.getOrderItemId() != null) {
            OrderItem orderItem = orderItemRepository.findById(reviewRequest.getOrderItemId())
                    .orElseThrow(() -> new ResourceNotFoundException("Order item not found with id: " + reviewRequest.getOrderItemId()));
            
            // Validate the order item belongs to the user and is for the correct product
            if (!orderItem.getOrder().getUser().getId().equals(userId)) {
                throw new IllegalArgumentException("You can only review products you have purchased");
            }
            
            if (!orderItem.getProduct().getId().equals(product.getId())) {
                throw new IllegalArgumentException("Order item product does not match the requested product");
            }
            
            // Mark as reviewed
            orderItem.setReviewed(true);
            orderItemRepository.save(orderItem);
        }
        
        // Create and save review
        Review review = new Review();
        review.setProduct(product);
        review.setUser(user);
        review.setRating(reviewRequest.getRating());
        review.setComment(reviewRequest.getComment());
        review.setCreatedAt(Instant.now());
        review.setUpdatedAt(Instant.now());
        
        Review savedReview = reviewRepository.save(review);
        
        return mapToReviewResponseDTO(savedReview);
    }
    
    @Transactional
    public ReviewResponseDTO updateReview(Long reviewId, ReviewRequestDTO reviewRequest, Long userId) {
        // Check if review exists and belongs to the user
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + reviewId));
        
        if (!review.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("You can only update your own reviews");
        }
        
        // Update review
        review.setRating(reviewRequest.getRating());
        if (reviewRequest.getComment() != null) {
            review.setComment(reviewRequest.getComment());
        }
        review.setUpdatedAt(Instant.now());
        
        Review updatedReview = reviewRepository.save(review);
        
        return mapToReviewResponseDTO(updatedReview);
    }
    
    @Transactional
    public void deleteReview(Long reviewId, Long userId) {
        // Check if review exists and belongs to the user
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + reviewId));
        
        if (!review.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("You can only delete your own reviews");
        }
        
        reviewRepository.delete(review);
    }
    
    private PagedResponse<ReviewResponseDTO> createReviewResponse(Page<Review> reviewPage) {
        List<Review> reviews = reviewPage.getNumberOfElements() == 0 ? 
                Collections.emptyList() : reviewPage.getContent();
        
        List<ReviewResponseDTO> content = reviews.stream()
                .map(this::mapToReviewResponseDTO)
                .collect(Collectors.toList());
        
        return new PagedResponse<>(
                content, 
                reviewPage.getNumber(), 
                reviewPage.getSize(), 
                reviewPage.getTotalElements(), 
                reviewPage.getTotalPages(), 
                reviewPage.isLast());
    }
    
    private ReviewResponseDTO mapToReviewResponseDTO(Review review) {
        return ReviewResponseDTO.builder()
                .id(review.getId())
                .productId(review.getProduct().getId())
                .productName(review.getProduct().getName())
                .userId(review.getUser().getId())
                .username(review.getUser().getUsername())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .build();
    }
} 