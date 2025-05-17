package khuong.com.tmbackend.product_service.payload;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponseDTO {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stockQuantity;
    private CategoryDTO category;
    private List<ProductVariantDTO> variants;
    private String imageUrl;
    private boolean active;
    private BigDecimal discountPercentage;
    private Instant createdAt;
    private Instant updatedAt;
    private Double averageRating;
    private Integer reviewCount;
} 