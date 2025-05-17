package khuong.com.tmbackend.purchase_service.dto;

import java.math.BigDecimal;
import java.time.Instant;

import khuong.com.tmbackend.purchase_service.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderHistoryDTO {
    private Long id;
    private Instant orderDate;
    private BigDecimal finalAmount;
    private OrderStatus status;
    private String paymentMethod;
    private String paymentStatus;
    private int itemCount;
    
    // Optional summary field - could contain first product's name + "and X more items"
    private String orderSummary;
} 