package khuong.com.tmbackend.purchase_service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartRequestDTO {
    @NotNull
    private Long productId;
    
    private Long variantId; // Optional if no variant
    
    @NotNull
    @Min(1)
    private Integer quantity;
    
    // For discount code application
    private String discountCode;
} 