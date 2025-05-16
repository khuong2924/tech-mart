package khuong.com.tmbackend.purchase_service.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import khuong.com.tmbackend.product_service.entity.Product;

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
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product; // Tham chiếu đến Product

    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "product_variant_id") // Nếu mua theo biến thể
    // private ProductVariant productVariant;

    private Integer quantity;
    private BigDecimal pricePerUnit; // Giá tại thời điểm mua
    private BigDecimal subtotal; // quantity * pricePerUnit

    // Constructors, Getters, Setters (Lombok)
}