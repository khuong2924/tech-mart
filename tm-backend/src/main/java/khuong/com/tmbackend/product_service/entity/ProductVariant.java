package khuong.com.tmbackend.product_service.entity;

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
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "product_variants")
public class ProductVariant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;  // Tên biến thể, ví dụ: "256GB, Đen"
    private String sku;   // Mã SKU riêng cho biến thể

    private BigDecimal priceAdjustment = BigDecimal.ZERO; // Điều chỉnh giá so với giá gốc
    private Integer stockQuantity; // Số lượng tồn kho riêng cho biến thể

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    @JsonIgnoreProperties("variants")
    private Product product;

    private String attributes; // Có thể lưu dưới dạng JSON, ví dụ: {color: "red", size: "512GB"}
    private String imageUrl; // Hình ảnh riêng cho biến thể (nếu có)
}

