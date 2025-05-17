package khuong.com.tmbackend.purchase_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import khuong.com.tmbackend.purchase_service.dto.CartDTO;
import khuong.com.tmbackend.purchase_service.dto.CartRequestDTO;
import khuong.com.tmbackend.purchase_service.service.CartService;
import khuong.com.tmbackend.user_service.repository.UserRepository;

@RestController
@RequestMapping("/api/cart")
public class CartController {
    
    @Autowired
    private CartService cartService;
    
    @Autowired
    private UserRepository userRepository;
    
    @GetMapping
    public ResponseEntity<CartDTO> getCurrentUserCart(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = getUserIdFromUserDetails(userDetails);
        CartDTO cart = cartService.getCartByUserId(userId);
        return ResponseEntity.ok(cart);
    }
    
    @PostMapping("/items")
    public ResponseEntity<CartDTO> addItemToCart(
            @Valid @RequestBody CartRequestDTO request,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        Long userId = getUserIdFromUserDetails(userDetails);
        CartDTO updatedCart = cartService.addItemToCart(userId, request);
        return ResponseEntity.ok(updatedCart);
    }
    
    @PutMapping("/items/{itemId}")
    public ResponseEntity<CartDTO> updateCartItem(
            @PathVariable Long itemId,
            @Valid @RequestBody CartRequestDTO request,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        Long userId = getUserIdFromUserDetails(userDetails);
        CartDTO updatedCart = cartService.updateCartItem(userId, itemId, request);
        return ResponseEntity.ok(updatedCart);
    }
    
    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<CartDTO> removeCartItem(
            @PathVariable Long itemId,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        Long userId = getUserIdFromUserDetails(userDetails);
        CartDTO updatedCart = cartService.removeCartItem(userId, itemId);
        return ResponseEntity.ok(updatedCart);
    }
    
    @DeleteMapping("/clear")
    public ResponseEntity<CartDTO> clearCart(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = getUserIdFromUserDetails(userDetails);
        CartDTO emptyCart = cartService.clearCart(userId);
        return ResponseEntity.ok(emptyCart);
    }
    
    @PostMapping("/apply-discount")
    public ResponseEntity<CartDTO> applyDiscountCode(
            @RequestParam String code,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        Long userId = getUserIdFromUserDetails(userDetails);
        CartDTO updatedCart = cartService.applyDiscountCode(userId, code);
        return ResponseEntity.ok(updatedCart);
    }
    
    @DeleteMapping("/remove-discount")
    public ResponseEntity<CartDTO> removeDiscountCode(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = getUserIdFromUserDetails(userDetails);
        CartDTO updatedCart = cartService.removeDiscountCode(userId);
        return ResponseEntity.ok(updatedCart);
    }
    
    // Helper method to get user ID from authenticated user
    private Long getUserIdFromUserDetails(UserDetails userDetails) {
        return userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"))
                .getId();
    }
} 