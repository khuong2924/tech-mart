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
public class OrderItemDTO {
    private Long id;
    private Long orderId;
    private Long productId;
    private String productName;
    private String productImage;
    private Long variantId;
    private String variantName;
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal subtotal;
    private String serialNumber;
    private String notes;
    private boolean reviewed;
} 