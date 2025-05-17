package khuong.com.tmbackend.purchase_service.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import khuong.com.tmbackend.purchase_service.entity.CartItem;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    
    List<CartItem> findByCartId(Long cartId);
    
    Optional<CartItem> findByCartIdAndProductId(Long cartId, Long productId);
    
    Optional<CartItem> findByCartIdAndProductIdAndProductVariantId(Long cartId, Long productId, Long variantId);
    
    void deleteByCartId(Long cartId);
} 