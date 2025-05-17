package khuong.com.tmbackend.purchase_service.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import khuong.com.tmbackend.product_service.entity.Product;
import khuong.com.tmbackend.product_service.entity.ProductVariant;
import khuong.com.tmbackend.product_service.repository.ProductRepository;
import khuong.com.tmbackend.product_service.repository.ProductVariantRepository;
import khuong.com.tmbackend.purchase_service.dto.EmailNotificationDTO;
import khuong.com.tmbackend.purchase_service.dto.OrderCheckoutResponseDTO;
import khuong.com.tmbackend.purchase_service.dto.OrderDTO;
import khuong.com.tmbackend.purchase_service.dto.OrderFilterRequest;
import khuong.com.tmbackend.purchase_service.dto.OrderHistoryDTO;
import khuong.com.tmbackend.purchase_service.dto.OrderItemDTO;
import khuong.com.tmbackend.purchase_service.dto.OrderRequestDTO;
import khuong.com.tmbackend.purchase_service.dto.PagedResponseDTO;
import khuong.com.tmbackend.purchase_service.entity.Cart;
import khuong.com.tmbackend.purchase_service.entity.CartItem;
import khuong.com.tmbackend.purchase_service.entity.DiscountCode;
import khuong.com.tmbackend.purchase_service.entity.Order;
import khuong.com.tmbackend.purchase_service.entity.OrderItem;
import khuong.com.tmbackend.purchase_service.enums.OrderStatus;
import khuong.com.tmbackend.purchase_service.exception.ResourceNotFoundException;
import khuong.com.tmbackend.purchase_service.repository.CartItemRepository;
import khuong.com.tmbackend.purchase_service.repository.CartRepository;
import khuong.com.tmbackend.purchase_service.repository.DiscountCodeRepository;
import khuong.com.tmbackend.purchase_service.repository.OrderItemRepository;
import khuong.com.tmbackend.purchase_service.repository.OrderRepository;
import khuong.com.tmbackend.user_service.entity.User;
import khuong.com.tmbackend.user_service.repository.UserRepository;

@Service
public class OrderService {
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private OrderItemRepository orderItemRepository;
    
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
    
    @Autowired
    private EmailService emailService; // This would need to be implemented
    
    public PagedResponseDTO<OrderDTO> getAllOrders(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? 
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Order> orders = orderRepository.findAll(pageable);
        
        return createOrderResponse(orders);
    }
    
    public PagedResponseDTO<OrderHistoryDTO> getUserOrderHistory(Long userId, int page, int size) {
        // Check if user exists
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("orderDate").descending());
        Page<Order> orders = orderRepository.findByUserId(userId, pageable);
        
        return createOrderHistoryResponse(orders);
    }
    
    public OrderDTO getOrderById(Long orderId, Long userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
        
        // Check if order belongs to user
        if (!order.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Order does not belong to user");
        }
        
        return mapToOrderDTO(order);
    }
    
    public PagedResponseDTO<OrderDTO> filterUserOrders(Long userId, OrderFilterRequest filterRequest) {
        // Check if user exists
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        // Create pageable with sorting
        Sort sort = filterRequest.getSortDirection().equalsIgnoreCase(Sort.Direction.ASC.name()) ? 
                Sort.by(filterRequest.getSortBy()).ascending() : Sort.by(filterRequest.getSortBy()).descending();
        
        Pageable pageable = PageRequest.of(filterRequest.getPage(), filterRequest.getSize(), sort);
        
        // Apply filters
        Page<Order> orders = orderRepository.filterOrders(
                userId,
                filterRequest.getStatus(),
                filterRequest.getStartDate(),
                filterRequest.getEndDate(),
                filterRequest.getPaymentMethod(),
                filterRequest.getPaymentStatus(),
                pageable);
        
        return createOrderResponse(orders);
    }
    
    @Transactional
    public OrderCheckoutResponseDTO createOrder(Long userId, OrderRequestDTO orderRequest) {
        // Check if user exists
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        // Get user's cart
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Cart is empty"));
        
        List<CartItem> cartItems = cartItemRepository.findByCartId(cart.getId());
        if (cartItems.isEmpty()) {
            throw new IllegalArgumentException("Cart is empty");
        }
        
        // Create order
        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(Instant.now());
        order.setStatus(OrderStatus.PENDING);
        order.setShippingAddress(orderRequest.getShippingAddress());
        order.setPaymentMethod(orderRequest.getPaymentMethod());
        
        // Set payment status based on payment method
        if ("COD".equalsIgnoreCase(orderRequest.getPaymentMethod())) {
            order.setPaymentStatus("PENDING");
        } else {
            order.setPaymentStatus("AWAITING_PAYMENT");
        }
        
        // Set amounts
        order.setTotalAmount(cart.getTotalAmount());
        order.setDiscountedAmount(cart.getDiscountAmount());
        order.setFinalAmount(cart.getFinalAmount());
        
        // Apply discount code if any
        if (cart.getAppliedDiscountCode() != null) {
            order.setAppliedDiscountCode(cart.getAppliedDiscountCode());
            
            // Update discount code usage
            DiscountCode discountCode = cart.getAppliedDiscountCode();
            if (discountCode.getUsageCount() != null) {
                discountCode.setUsageCount(discountCode.getUsageCount() + 1);
                discountCodeRepository.save(discountCode);
            }
        } else if (orderRequest.getDiscountCode() != null && !orderRequest.getDiscountCode().isEmpty()) {
            // Try to apply discount code from request
            DiscountCode discountCode = discountCodeRepository.findValidDiscountCode(
                    orderRequest.getDiscountCode(), Instant.now())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid or expired discount code"));
            
            // Check minimum order amount
            if (discountCode.getMinimumOrderAmount() != null && 
                cart.getTotalAmount().compareTo(discountCode.getMinimumOrderAmount()) < 0) {
                throw new IllegalArgumentException("Minimum order amount not met. Required: " + discountCode.getMinimumOrderAmount());
            }
            
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
            
            // Update order amounts
            order.setAppliedDiscountCode(discountCode);
            order.setDiscountedAmount(discountAmount);
            order.setFinalAmount(cart.getTotalAmount().subtract(discountAmount));
            
            // Update discount code usage
            if (discountCode.getUsageCount() != null) {
                discountCode.setUsageCount(discountCode.getUsageCount() + 1);
                discountCodeRepository.save(discountCode);
            }
        }
        
        // Save order
        Order savedOrder = orderRepository.save(order);
        
        // Create order items from cart items
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(savedOrder);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setProductVariant(cartItem.getProductVariant());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getUnitPrice());
            orderItem.setSubtotal(cartItem.getSubtotal());
            
            if (orderRequest.getNotes() != null) {
                orderItem.setNotes(orderRequest.getNotes());
            }
            
            // Save order item
            orderItems.add(orderItemRepository.save(orderItem));
            
            // Update product stock
            updateProductStock(cartItem);
        }
        
        // Clear cart
        cartItemRepository.deleteByCartId(cart.getId());
        cart.setTotalAmount(BigDecimal.ZERO);
        cart.setDiscountAmount(BigDecimal.ZERO);
        cart.setFinalAmount(BigDecimal.ZERO);
        cart.setAppliedDiscountCode(null);
        cartRepository.save(cart);
        
        // Generate order number
        String orderNumber = generateOrderNumber(savedOrder.getId());
        
        // Send email notification
        sendOrderConfirmationEmail(savedOrder, orderItems, user);
        
        // Create response
        return OrderCheckoutResponseDTO.builder()
                .orderId(savedOrder.getId())
                .orderNumber(orderNumber)
                .paymentStatus(savedOrder.getPaymentStatus())
                .totalAmount(savedOrder.getFinalAmount())
                .estimatedDeliveryDate("3-5 business days") // This could be calculated based on shipping method
                .confirmationMessage("Your order has been placed successfully.")
                .build();
    }
    
    @Transactional
    public OrderDTO updateOrderStatus(Long orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
        
        order.setStatus(status);
        
        // Update payment status if needed
        if (status == OrderStatus.DELIVERED) {
            order.setPaymentStatus("PAID");
        } else if (status == OrderStatus.CANCELED) {
            order.setPaymentStatus("CANCELED");
            
            // Return stock for canceled orders
            List<OrderItem> orderItems = orderItemRepository.findByOrderId(orderId);
            for (OrderItem item : orderItems) {
                returnProductStock(item);
            }
        }
        
        Order updatedOrder = orderRepository.save(order);
        return mapToOrderDTO(updatedOrder);
    }
    
    // Helper methods
    private void updateProductStock(CartItem cartItem) {
        Product product = cartItem.getProduct();
        ProductVariant variant = cartItem.getProductVariant();
        
        // Update product or variant stock
        if (variant != null) {
            variant.setStockQuantity(variant.getStockQuantity() - cartItem.getQuantity());
            productVariantRepository.save(variant);
        } else {
            product.setStockQuantity(product.getStockQuantity() - cartItem.getQuantity());
            productRepository.save(product);
        }
    }
    
    private void returnProductStock(OrderItem orderItem) {
        Product product = orderItem.getProduct();
        ProductVariant variant = orderItem.getProductVariant();
        
        // Return stock to product or variant
        if (variant != null) {
            variant.setStockQuantity(variant.getStockQuantity() + orderItem.getQuantity());
            productVariantRepository.save(variant);
        } else {
            product.setStockQuantity(product.getStockQuantity() + orderItem.getQuantity());
            productRepository.save(product);
        }
    }
    
    private String generateOrderNumber(Long orderId) {
        // Generate a unique order number
        return "TM-" + String.format("%06d", orderId) + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    private void sendOrderConfirmationEmail(Order order, List<OrderItem> orderItems, User user) {
        // Map order items to DTOs
        List<OrderItemDTO> itemDTOs = orderItems.stream()
                .map(item -> OrderItemDTO.builder()
                        .id(item.getId())
                        .productId(item.getProduct().getId())
                        .productName(item.getProduct().getName())
                        .productImage(item.getProduct().getImageUrl())
                        .variantId(item.getProductVariant() != null ? item.getProductVariant().getId() : null)
                        .variantName(item.getProductVariant() != null ? item.getProductVariant().getName() : null)
                        .quantity(item.getQuantity())
                        .price(item.getPrice())
                        .subtotal(item.getSubtotal())
                        .build())
                .collect(Collectors.toList());
        
        // Create email notification
        EmailNotificationDTO emailDTO = EmailNotificationDTO.builder()
                .to(user.getEmail())
                .subject("Order Confirmation - " + generateOrderNumber(order.getId()))
                .customerName(user.getFullName())
                .orderId(order.getId())
                .orderNumber(generateOrderNumber(order.getId()))
                .orderDate(order.getOrderDate())
                .totalAmount(order.getFinalAmount())
                .shippingAddress(order.getShippingAddress())
                .paymentMethod(order.getPaymentMethod())
                .paymentStatus(order.getPaymentStatus())
                .items(itemDTOs)
                .estimatedDeliveryDate("3-5 business days")
                .build();
        
        // Send email
        // emailService.sendOrderConfirmationEmail(emailDTO);
        // Note: Uncomment this when EmailService is implemented
    }
    
    private PagedResponseDTO<OrderDTO> createOrderResponse(Page<Order> orderPage) {
        List<Order> orders = orderPage.getNumberOfElements() == 0 ? 
                Collections.emptyList() : orderPage.getContent();
        
        List<OrderDTO> content = orders.stream()
                .map(this::mapToOrderDTO)
                .collect(Collectors.toList());
        
        return new PagedResponseDTO<>(
                content, 
                orderPage.getNumber(), 
                orderPage.getSize(), 
                orderPage.getTotalElements(), 
                orderPage.getTotalPages(), 
                orderPage.isLast());
    }
    
    private PagedResponseDTO<OrderHistoryDTO> createOrderHistoryResponse(Page<Order> orderPage) {
        List<Order> orders = orderPage.getNumberOfElements() == 0 ? 
                Collections.emptyList() : orderPage.getContent();
        
        List<OrderHistoryDTO> content = orders.stream()
                .map(this::mapToOrderHistoryDTO)
                .collect(Collectors.toList());
        
        return new PagedResponseDTO<>(
                content, 
                orderPage.getNumber(), 
                orderPage.getSize(), 
                orderPage.getTotalElements(), 
                orderPage.getTotalPages(), 
                orderPage.isLast());
    }
    
    private OrderDTO mapToOrderDTO(Order order) {
        List<OrderItem> orderItems = orderItemRepository.findByOrderId(order.getId());
        
        List<OrderItemDTO> itemDTOs = orderItems.stream()
                .map(item -> OrderItemDTO.builder()
                        .id(item.getId())
                        .orderId(order.getId())
                        .productId(item.getProduct().getId())
                        .productName(item.getProduct().getName())
                        .productImage(item.getProduct().getImageUrl())
                        .variantId(item.getProductVariant() != null ? item.getProductVariant().getId() : null)
                        .variantName(item.getProductVariant() != null ? item.getProductVariant().getName() : null)
                        .quantity(item.getQuantity())
                        .price(item.getPrice())
                        .subtotal(item.getSubtotal())
                        .serialNumber(item.getSerialNumber())
                        .notes(item.getNotes())
                        .reviewed(item.isReviewed())
                        .build())
                .collect(Collectors.toList());
        
        return OrderDTO.builder()
                .id(order.getId())
                .userId(order.getUser().getId())
                .username(order.getUser().getUsername())
                .orderItems(itemDTOs)
                .orderDate(order.getOrderDate())
                .totalAmount(order.getTotalAmount())
                .discountedAmount(order.getDiscountedAmount())
                .finalAmount(order.getFinalAmount())
                .status(order.getStatus())
                .shippingAddress(order.getShippingAddress())
                .paymentMethod(order.getPaymentMethod())
                .paymentStatus(order.getPaymentStatus())
                .discountCodeUsed(order.getAppliedDiscountCode() != null ? order.getAppliedDiscountCode().getCode() : null)
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }
    
    private OrderHistoryDTO mapToOrderHistoryDTO(Order order) {
        List<OrderItem> orderItems = orderItemRepository.findByOrderId(order.getId());
        
        // Create order summary (e.g., "iPhone 15 and 2 more items")
        String orderSummary = "";
        if (!orderItems.isEmpty()) {
            OrderItem firstItem = orderItems.get(0);
            String firstItemName = firstItem.getProduct().getName();
            
            if (orderItems.size() > 1) {
                orderSummary = firstItemName + " and " + (orderItems.size() - 1) + " more item(s)";
            } else {
                orderSummary = firstItemName;
            }
        }
        
        return OrderHistoryDTO.builder()
                .id(order.getId())
                .orderDate(order.getOrderDate())
                .finalAmount(order.getFinalAmount())
                .status(order.getStatus())
                .paymentMethod(order.getPaymentMethod())
                .paymentStatus(order.getPaymentStatus())
                .itemCount(orderItems.size())
                .orderSummary(orderSummary)
                .build();
    }
} 