package khuong.com.tmbackend.purchase_service.dto;

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
public class EmailNotificationDTO {
    private String to;
    private String subject;
    private String customerName;
    private Long orderId;
    private String orderNumber;
    private Instant orderDate;
    private BigDecimal totalAmount;
    private String shippingAddress;
    private String paymentMethod;
    private String paymentStatus;
    private List<OrderItemDTO> items;
    private String estimatedDeliveryDate;
    private String trackingNumber;
    private String specialInstructions;
} 