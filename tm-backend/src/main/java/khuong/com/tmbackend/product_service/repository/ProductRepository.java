package khuong.com.tmbackend.product_service.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import khuong.com.tmbackend.product_service.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    // Find products by category
    Page<Product> findByCategoryId(Long categoryId, Pageable pageable);
    
    // Search products by keyword in name or description
    @Query("SELECT p FROM Product p WHERE p.active = true AND " +
           "(LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Product> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
    
    // Filter products by price range
    Page<Product> findByPriceBetweenAndActive(BigDecimal minPrice, BigDecimal maxPrice, boolean active, Pageable pageable);
    
    // Filter products by stock availability
    Page<Product> findByStockQuantityGreaterThanAndActive(int minStock, boolean active, Pageable pageable);
    
    // Combined search and filter
    @Query("SELECT p FROM Product p WHERE " +
           "(:categoryId IS NULL OR p.category.id = :categoryId) AND " +
           "(:keyword IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
           "(:minPrice IS NULL OR p.price >= :minPrice) AND " +
           "(:maxPrice IS NULL OR p.price <= :maxPrice) AND " +
           "(:inStock IS NULL OR (:inStock = true AND p.stockQuantity > 0) OR " +
           "(:inStock = false AND p.stockQuantity = 0)) AND " +
           "p.active = true")
    Page<Product> filterProducts(
            @Param("categoryId") Long categoryId,
            @Param("keyword") String keyword,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("inStock") Boolean inStock,
            Pageable pageable);
    
    // Find featured products (for homepage)
    @Query(value = "SELECT * FROM products p WHERE p.active = true ORDER BY p.created_at DESC LIMIT :limit", 
           nativeQuery = true)
    List<Product> findLatestProducts(@Param("limit") int limit);
    
    // Count products by category
    long countByCategoryId(Long categoryId);
} 