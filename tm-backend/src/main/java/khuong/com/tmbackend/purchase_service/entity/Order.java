package khuong.com.tmbackend.purchase_service.entity;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import khuong.com.tmbackend.purchase_service.enums.OrderStatus;
import khuong.com.tmbackend.user_service.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


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
    @JsonIgnoreProperties({"orders", "password", "roles"})
    private User user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("order")
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
    @JsonIgnoreProperties("orders")
    private DiscountCode appliedDiscountCode;

    // Timestamps
    private Instant createdAt;
    private Instant updatedAt;
    
    @PrePersist
    protected void onCreate() {
        orderDate = Instant.now();
        createdAt = Instant.now();
        updatedAt = Instant.now();
        if (status == null) {
            status = OrderStatus.PENDING;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }
}

