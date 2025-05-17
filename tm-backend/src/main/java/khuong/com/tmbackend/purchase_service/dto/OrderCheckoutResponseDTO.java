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
public class OrderCheckoutResponseDTO {
    private Long orderId;
    private String orderNumber;
    private String paymentStatus;
    private BigDecimal totalAmount;
    private String estimatedDeliveryDate;
    private String confirmationMessage;
    
    // For online payments
    private String paymentUrl; // URL to payment gateway if applicable
    private String transactionId; // Payment transaction ID if applicable
} 