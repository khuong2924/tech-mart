package khuong.com.tmbackend.purchase_service.entity;
import java.math.BigDecimal;
import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "discount_codes")
public class DiscountCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code;

    private BigDecimal discountPercentage; // Hoặc fixedAmount
    // private BigDecimal discountAmount;
    private Instant validFrom;
    private Instant validTo;
    private boolean active = true;
    private Integer usageLimit; // Số lần sử dụng tối đa
    private Integer timesUsed = 0; // Số lần đã sử dụng

    // Constructors, Getters, Setters (Lombok)
}