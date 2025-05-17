package khuong.com.tmbackend.purchase_service.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import khuong.com.tmbackend.purchase_service.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    private Long id;
    private Long userId;
    private String username;
    private List<OrderItemDTO> orderItems;
    private Instant orderDate;
    private BigDecimal totalAmount;
    private BigDecimal discountedAmount;
    private BigDecimal finalAmount;
    private OrderStatus status;
    private String shippingAddress;
    private String paymentMethod;
    private String paymentStatus;
    private String discountCodeUsed;
    private Instant createdAt;
    private Instant updatedAt;
} 