package khuong.com.tmbackend.product_service.entity;

import java.math.BigDecimal;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    private String name; // Ví dụ: "Màu Xanh, Size L"
    // Hoặc các thuộc tính riêng:
    // private String color;
    // private String size;
    private BigDecimal priceModifier; // Chênh lệch giá so với sản phẩm gốc (có thể âm hoặc dương)
    private Integer stockQuantity;
    private String sku; // Mã SKU cho biến thể

    // Constructors, Getters, Setters (Lombok)
}

