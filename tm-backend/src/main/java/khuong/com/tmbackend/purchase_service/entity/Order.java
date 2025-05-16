package khuong.com.tmbackend.purchase_service.entity;
import jakarta.persistence.*;
import khuong.com.tmbackend.user_service.entity.User;
import khuong.com.tmbackend.purchase_service.entity.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import lombok.*;

import khuong.com.tmbackend.purchase_service.entity.OrderItem;
import khuong.com.tmbackend.purchase_service.entity.OrderStatus;
import khuong.com.tmbackend.purchase_service.entity.DiscountCode;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    private Instant orderDate;
    private BigDecimal totalAmount; // Tổng tiền trước giảm giá
    private BigDecimal discountedAmount; // Số tiền được giảm
    private BigDecimal finalAmount; // Tổng tiền sau giảm giá

    @Enumerated(EnumType.STRING)
    private OrderStatus status; // Enum: PENDING, PROCESSING, SHIPPED, DELIVERED, CANCELED

    private String shippingAddress;
    private String paymentMethod; // Ví dụ: COD, ONLINE_PAYMENT
    private String paymentStatus; // Ví dụ: PENDING, PAID, FAILED

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "discount_code_id")
    private DiscountCode appliedDiscountCode;

    // Timestamps
    private Instant createdAt;
    private Instant updatedAt;

}

