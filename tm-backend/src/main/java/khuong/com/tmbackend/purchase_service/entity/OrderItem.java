package khuong.com.tmbackend.purchase_service.entity;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import khuong.com.tmbackend.product_service.entity.Product;
import khuong.com.tmbackend.product_service.entity.ProductVariant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "order_items")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    @JsonIgnoreProperties("orderItems")
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    @JsonIgnoreProperties({"variants", "category"})
    private Product product;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_variant_id")
    @JsonIgnoreProperties("product")
    private ProductVariant productVariant;

    private Integer quantity;
    private BigDecimal price; // Giá tại thời điểm mua
    private BigDecimal subtotal; // Tổng phụ (price * quantity)
    
    private String serialNumber; // Số serial/IMEI cho sản phẩm điện tử (nếu có)
    
    // Thông tin khác
    private String notes; // Ghi chú cho sản phẩm
    private boolean reviewed = false; // Đánh dấu đã đánh giá chưa
}