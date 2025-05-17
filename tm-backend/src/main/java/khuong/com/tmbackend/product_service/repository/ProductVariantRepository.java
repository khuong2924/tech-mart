package khuong.com.tmbackend.product_service.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import khuong.com.tmbackend.product_service.entity.ProductVariant;

@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {
    
    List<ProductVariant> findByProductId(Long productId);
    
    Optional<ProductVariant> findByProductIdAndId(Long productId, Long variantId);
    
    Optional<ProductVariant> findBySku(String sku);
} 