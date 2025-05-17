package khuong.com.tmbackend.product_service.payload;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductFilterRequest {
    // Search and filter criteria
    private String keyword;
    private Long categoryId;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private Boolean inStock;
    
    // Sorting criteria
    private String sortBy = "id"; // Default sort by id
    private String sortDirection = "asc"; // asc or desc
    
    // Pagination parameters
    private int page = 0;
    private int size = 10;
} 