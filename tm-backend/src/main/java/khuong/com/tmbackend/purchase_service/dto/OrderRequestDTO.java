package khuong.com.tmbackend.purchase_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequestDTO {
    @NotBlank
    private String shippingAddress;
    
    @NotBlank
    private String paymentMethod; // COD, ONLINE_PAYMENT, etc.
    
    // Optional discount code
    private String discountCode;
    
    // Additional order notes
    private String notes;
} 