package khuong.com.tmbackend.purchase_service.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import khuong.com.tmbackend.product_service.entity.Product;
import khuong.com.tmbackend.product_service.entity.ProductVariant;
import khuong.com.tmbackend.product_service.repository.ProductRepository;
import khuong.com.tmbackend.product_service.repository.ProductVariantRepository;
import khuong.com.tmbackend.purchase_service.dto.CartDTO;
import khuong.com.tmbackend.purchase_service.dto.CartItemDTO;
import khuong.com.tmbackend.purchase_service.dto.CartRequestDTO;
import khuong.com.tmbackend.purchase_service.entity.Cart;
import khuong.com.tmbackend.purchase_service.entity.CartItem;
import khuong.com.tmbackend.purchase_service.entity.DiscountCode;
import khuong.com.tmbackend.purchase_service.exception.ResourceNotFoundException;
import khuong.com.tmbackend.purchase_service.repository.CartItemRepository;
import khuong.com.tmbackend.purchase_service.repository.CartRepository;
import khuong.com.tmbackend.purchase_service.repository.DiscountCodeRepository;
import khuong.com.tmbackend.user_service.entity.User;
import khuong.com.tmbackend.user_service.repository.UserRepository;

@Service
public class CartService {
    
    @Autowired
    private CartRepository cartRepository;
    
    @Autowired
    private CartItemRepository cartItemRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private ProductVariantRepository productVariantRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private DiscountCodeRepository discountCodeRepository;
    
    public CartDTO getCartByUserId(Long userId) {
        Cart cart = getOrCreateCart(userId);
        return mapToCartDTO(cart);
    }
    
    @Transactional
    public CartDTO addItemToCart(Long userId, CartRequestDTO request) {
        // Verify product exists
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + request.getProductId()));
        
        // Check if product is in stock
        if (product.getStockQuantity() < request.getQuantity()) {
            throw new IllegalArgumentException("Product is out of stock. Available: " + product.getStockQuantity());
        }
        
        // Get or create cart for user
        Cart cart = getOrCreateCart(userId);
        
        // Check if product has variants and if variant was specified
        ProductVariant variant = null;
        if (request.getVariantId() != null) {
            variant = productVariantRepository.findById(request.getVariantId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product variant not found with id: " + request.getVariantId()));
            
            // Verify variant belongs to product
            if (!variant.getProduct().getId().equals(product.getId())) {
                throw new IllegalArgumentException("Variant does not belong to the specified product");
            }
            
            // Check variant stock
            if (variant.getStockQuantity() < request.getQuantity()) {
                throw new IllegalArgumentException("Variant is out of stock. Available: " + variant.getStockQuantity());
            }
        }
        
        // Check if item already exists in cart
        CartItem cartItem;
        if (variant != null) {
            cartItem = cartItemRepository.findByCartIdAndProductIdAndProductVariantId(
                    cart.getId(), product.getId(), variant.getId()).orElse(null);
        } else {
            cartItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId()).orElse(null);
        }
        
        // Update quantity or add new item
        if (cartItem != null) {
            cartItem.setQuantity(cartItem.getQuantity() + request.getQuantity());
            // Update subtotal
            BigDecimal unitPrice = calculateProductPrice(product, variant);
            cartItem.setSubtotal(unitPrice.multiply(new BigDecimal(cartItem.getQuantity())));
        } else {
            cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            if (variant != null) {
                cartItem.setProductVariant(variant);
            }
            cartItem.setQuantity(request.getQuantity());
            
            // Calculate price
            BigDecimal unitPrice = calculateProductPrice(product, variant);
            cartItem.setUnitPrice(unitPrice);
            cartItem.setSubtotal(unitPrice.multiply(new BigDecimal(request.getQuantity())));
        }
        
        // Save cart item
        cartItemRepository.save(cartItem);
        
        // Update cart total
        updateCartTotals(cart);
        
        // Apply discount code if provided
        if (request.getDiscountCode() != null && !request.getDiscountCode().isEmpty()) {
            return applyDiscountCode(userId, request.getDiscountCode());
        }
        
        return mapToCartDTO(cart);
    }
    
    @Transactional
    public CartDTO updateCartItem(Long userId, Long cartItemId, CartRequestDTO request) {
        // Get cart for user
        Cart cart = getOrCreateCart(userId);
        
        // Find cart item
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found with id: " + cartItemId));
        
        // Verify item belongs to user's cart
        if (!cartItem.getCart().getId().equals(cart.getId())) {
            throw new IllegalArgumentException("Cart item does not belong to user's cart");
        }
        
        // Check if product is in stock
        Product product = cartItem.getProduct();
        ProductVariant variant = cartItem.getProductVariant();
        
        if (variant != null) {
            if (variant.getStockQuantity() < request.getQuantity()) {
                throw new IllegalArgumentException("Variant is out of stock. Available: " + variant.getStockQuantity());
            }
        } else {
            if (product.getStockQuantity() < request.getQuantity()) {
                throw new IllegalArgumentException("Product is out of stock. Available: " + product.getStockQuantity());
            }
        }
        
        // Update quantity and subtotal
        cartItem.setQuantity(request.getQuantity());
        cartItem.setSubtotal(cartItem.getUnitPrice().multiply(new BigDecimal(request.getQuantity())));
        
        // Save cart item
        cartItemRepository.save(cartItem);
        
        // Update cart total
        updateCartTotals(cart);
        
        return mapToCartDTO(cart);
    }
    
    @Transactional
    public CartDTO removeCartItem(Long userId, Long cartItemId) {
        // Get cart for user
        Cart cart = getOrCreateCart(userId);
        
        // Find cart item
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found with id: " + cartItemId));
        
        // Verify item belongs to user's cart
        if (!cartItem.getCart().getId().equals(cart.getId())) {
            throw new IllegalArgumentException("Cart item does not belong to user's cart");
        }
        
        // Remove item
        cartItemRepository.delete(cartItem);
        
        // Update cart total
        updateCartTotals(cart);
        
        return mapToCartDTO(cart);
    }
    
    @Transactional
    public CartDTO clearCart(Long userId) {
        // Get cart for user
        Cart cart = getOrCreateCart(userId);
        
        // Remove all items
        cartItemRepository.deleteByCartId(cart.getId());
        
        // Reset cart totals
        cart.setTotalAmount(BigDecimal.ZERO);
        cart.setDiscountAmount(BigDecimal.ZERO);
        cart.setFinalAmount(BigDecimal.ZERO);
        cart.setAppliedDiscountCode(null);
        cartRepository.save(cart);
        
        return mapToCartDTO(cart);
    }
    
    @Transactional
    public CartDTO applyDiscountCode(Long userId, String code) {
        // Get cart for user
        Cart cart = getOrCreateCart(userId);
        
        // Find valid discount code
        DiscountCode discountCode = discountCodeRepository.findValidDiscountCode(code, Instant.now())
                .orElseThrow(() -> new IllegalArgumentException("Invalid or expired discount code"));
        
        // Check minimum order amount
        if (discountCode.getMinimumOrderAmount() != null && 
            cart.getTotalAmount().compareTo(discountCode.getMinimumOrderAmount()) < 0) {
            throw new IllegalArgumentException("Minimum order amount not met. Required: " + discountCode.getMinimumOrderAmount());
        }
        
        // Apply discount code
        cart.setAppliedDiscountCode(discountCode);
        
        // Calculate discount
        BigDecimal discountAmount = BigDecimal.ZERO;
        if (discountCode.getDiscountPercentage() != null) {
            // Percentage discount
            discountAmount = cart.getTotalAmount().multiply(discountCode.getDiscountPercentage().divide(new BigDecimal("100")));
        } else if (discountCode.getDiscountAmount() != null) {
            // Fixed amount discount
            discountAmount = discountCode.getDiscountAmount();
        }
        
        // Apply maximum discount if needed
        if (discountCode.getMaximumDiscountAmount() != null && 
            discountAmount.compareTo(discountCode.getMaximumDiscountAmount()) > 0) {
            discountAmount = discountCode.getMaximumDiscountAmount();
        }
        
        // Update cart totals
        cart.setDiscountAmount(discountAmount);
        cart.setFinalAmount(cart.getTotalAmount().subtract(discountAmount));
        cartRepository.save(cart);
        
        return mapToCartDTO(cart);
    }
    
    @Transactional
    public CartDTO removeDiscountCode(Long userId) {
        // Get cart for user
        Cart cart = getOrCreateCart(userId);
        
        // Remove discount code
        cart.setAppliedDiscountCode(null);
        cart.setDiscountAmount(BigDecimal.ZERO);
        cart.setFinalAmount(cart.getTotalAmount());
        cartRepository.save(cart);
        
        return mapToCartDTO(cart);
    }
    
    // Helper methods
    private Cart getOrCreateCart(Long userId) {
        // Check if user exists
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        // Get existing cart or create new one
        return cartRepository.findByUserId(userId).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setUser(user);
            newCart.setTotalAmount(BigDecimal.ZERO);
            newCart.setDiscountAmount(BigDecimal.ZERO);
            newCart.setFinalAmount(BigDecimal.ZERO);
            return cartRepository.save(newCart);
        });
    }
    
    private void updateCartTotals(Cart cart) {
        // Get all items in cart
        List<CartItem> items = cartItemRepository.findByCartId(cart.getId());
        
        // Calculate total
        BigDecimal total = items.stream()
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Update cart
        cart.setTotalAmount(total);
        
        // Apply discount if there is one
        if (cart.getAppliedDiscountCode() != null) {
            DiscountCode discountCode = cart.getAppliedDiscountCode();
            
            // Calculate discount
            BigDecimal discountAmount = BigDecimal.ZERO;
            if (discountCode.getDiscountPercentage() != null) {
                // Percentage discount
                discountAmount = total.multiply(discountCode.getDiscountPercentage().divide(new BigDecimal("100")));
            } else if (discountCode.getDiscountAmount() != null) {
                // Fixed amount discount
                discountAmount = discountCode.getDiscountAmount();
            }
            
            // Apply maximum discount if needed
            if (discountCode.getMaximumDiscountAmount() != null && 
                discountAmount.compareTo(discountCode.getMaximumDiscountAmount()) > 0) {
                discountAmount = discountCode.getMaximumDiscountAmount();
            }
            
            cart.setDiscountAmount(discountAmount);
            cart.setFinalAmount(total.subtract(discountAmount));
        } else {
            cart.setDiscountAmount(BigDecimal.ZERO);
            cart.setFinalAmount(total);
        }
        
        cartRepository.save(cart);
    }
    
    private BigDecimal calculateProductPrice(Product product, ProductVariant variant) {
        BigDecimal basePrice = product.getPrice();
        
        // Apply product discount if any
        if (product.getDiscountPercentage() != null && product.getDiscountPercentage().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal discountAmount = basePrice.multiply(product.getDiscountPercentage().divide(new BigDecimal("100")));
            basePrice = basePrice.subtract(discountAmount);
        }
        
        // Apply variant price adjustment if any
        if (variant != null && variant.getPriceAdjustment() != null) {
            basePrice = basePrice.add(variant.getPriceAdjustment());
        }
        
        return basePrice;
    }
    
    private CartDTO mapToCartDTO(Cart cart) {
        List<CartItem> items = cartItemRepository.findByCartId(cart.getId());
        
        List<CartItemDTO> itemDTOs = items.stream()
                .map(item -> {
                    Product product = item.getProduct();
                    ProductVariant variant = item.getProductVariant();
                    
                    return CartItemDTO.builder()
                            .id(item.getId())
                            .productId(product.getId())
                            .productName(product.getName())
                            .productImage(product.getImageUrl())
                            .productVariantId(variant != null ? variant.getId() : null)
                            .variantName(variant != null ? variant.getName() : null)
                            .quantity(item.getQuantity())
                            .unitPrice(item.getUnitPrice())
                            .subtotal(item.getSubtotal())
                            .build();
                })
                .collect(Collectors.toList());
        
        return CartDTO.builder()
                .id(cart.getId())
                .userId(cart.getUser().getId())
                .items(itemDTOs)
                .totalItems(items.size())
                .totalAmount(cart.getTotalAmount())
                .discountAmount(cart.getDiscountAmount())
                .finalAmount(cart.getFinalAmount())
                .appliedDiscountCode(cart.getAppliedDiscountCode() != null ? cart.getAppliedDiscountCode().getCode() : null)
                .build();
    }
} 