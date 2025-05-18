package khuong.com.tmbackend.purchase_service.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateDiscountCodeRequest {
    private String code;
    private String description;
    private BigDecimal discountAmount; // Fixed amount discount
    private BigDecimal discountPercent; // Percentage discount (0-100)
    private BigDecimal minOrderValue; // Minimum order value to apply
    private BigDecimal maxDiscount; // Maximum discount amount
    private String startDate; // Format: YYYY-MM-DD
    private String endDate; // Format: YYYY-MM-DD
    private Integer usageLimit; // Maximum usage count
    private boolean isActive = true;
} 