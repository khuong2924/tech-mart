package khuong.com.tmbackend.product_service.payload;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductVariantDTO {
    private Long id;
    private String name;
    private String sku;
    private BigDecimal priceAdjustment;
    private Integer stockQuantity;
    private String attributes;
    private String imageUrl;
    private BigDecimal finalPrice; // Calculated price after adjustment
} 