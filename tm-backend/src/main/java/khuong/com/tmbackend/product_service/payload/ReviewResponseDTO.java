package khuong.com.tmbackend.product_service.payload;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponseDTO {
    private Long id;
    private Long productId;
    private String productName;
    private Long userId;
    private String username;
    private Integer rating;
    private String comment;
    private Instant createdAt;
    private Instant updatedAt;
} 