package khuong.com.tmbackend.purchase_service.dto;

import java.math.BigDecimal;
import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiscountCodeDTO {
    private Long id;
    private String code;
    private String description;
    private BigDecimal discountAmount; // Fixed amount
    private BigDecimal discountPercentage; // Percentage (0-100)
    private BigDecimal minimumOrderAmount; // Minimum order amount to be eligible
    private BigDecimal maximumDiscountAmount; // Maximum discount allowed
    private Instant validFrom;
    private Instant validTo;
    private Integer usageLimit; // Maximum number of uses
    private Integer usageCount; // Current number of uses
    private boolean active;
} 