package khuong.com.tmbackend.purchase_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import khuong.com.tmbackend.purchase_service.dto.OrderCheckoutResponseDTO;
import khuong.com.tmbackend.purchase_service.dto.OrderDTO;
import khuong.com.tmbackend.purchase_service.dto.OrderFilterRequest;
import khuong.com.tmbackend.purchase_service.dto.OrderHistoryDTO;
import khuong.com.tmbackend.purchase_service.dto.OrderRequestDTO;
import khuong.com.tmbackend.purchase_service.dto.PagedResponseDTO;
import khuong.com.tmbackend.purchase_service.enums.OrderStatus;
import khuong.com.tmbackend.purchase_service.service.OrderService;
import khuong.com.tmbackend.user_service.repository.UserRepository;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    
    @Autowired
    private OrderService orderService;
    
    @Autowired
    private UserRepository userRepository;
    
    @GetMapping
    public ResponseEntity<PagedResponseDTO<OrderHistoryDTO>> getUserOrderHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        Long userId = getUserIdFromUserDetails(userDetails);
        PagedResponseDTO<OrderHistoryDTO> orderHistory = orderService.getUserOrderHistory(userId, page, size);
        return ResponseEntity.ok(orderHistory);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO> getOrderById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        Long userId = getUserIdFromUserDetails(userDetails);
        OrderDTO order = orderService.getOrderById(id, userId);
        return ResponseEntity.ok(order);
    }
    
    @PostMapping("/filter")
    public ResponseEntity<PagedResponseDTO<OrderDTO>> filterUserOrders(
            @RequestBody OrderFilterRequest filterRequest,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        Long userId = getUserIdFromUserDetails(userDetails);
        PagedResponseDTO<OrderDTO> orders = orderService.filterUserOrders(userId, filterRequest);
        return ResponseEntity.ok(orders);
    }
    
    @PostMapping
    public ResponseEntity<OrderCheckoutResponseDTO> createOrder(
            @Valid @RequestBody OrderRequestDTO orderRequest,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        Long userId = getUserIdFromUserDetails(userDetails);
        OrderCheckoutResponseDTO checkoutResponse = orderService.createOrder(userId, orderRequest);
        return new ResponseEntity<>(checkoutResponse, HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderDTO> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam OrderStatus status) {
        
        OrderDTO updatedOrder = orderService.updateOrderStatus(id, status);
        return ResponseEntity.ok(updatedOrder);
    }
    
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PagedResponseDTO<OrderDTO>> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "orderDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        PagedResponseDTO<OrderDTO> orders = orderService.getAllOrders(page, size, sortBy, sortDir);
        return ResponseEntity.ok(orders);
    }
    
    // Helper method to get user ID from authenticated user
    private Long getUserIdFromUserDetails(UserDetails userDetails) {
        return userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"))
                .getId();
    }
} 