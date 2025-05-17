package khuong.com.tmbackend.purchase_service.entity;
import java.math.BigDecimal;
import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "discount_codes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DiscountCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code;
    
    private String description;

    private BigDecimal discountPercentage; // Phần trăm giảm giá
    
    @Column(name = "discount_amount")
    private BigDecimal discountAmount; // Số tiền giảm giá cố định
    
    @Column(name = "minimum_order_amount")
    private BigDecimal minimumOrderAmount; // Giá trị đơn hàng tối thiểu để áp dụng
    
    @Column(name = "maximum_discount_amount")
    private BigDecimal maximumDiscountAmount; // Giảm giá tối đa có thể áp dụng
    
    private Instant validFrom;
    private Instant validTo;
    private boolean active = true;
    
    @Column(name = "usage_limit")
    private Integer usageLimit; // Số lần sử dụng tối đa
    
    @Column(name = "usage_count")
    private Integer usageCount = 0; // Số lần đã sử dụng
}