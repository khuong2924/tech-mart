package khuong.com.tmbackend.purchase_service.repository;

import java.time.Instant;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import khuong.com.tmbackend.purchase_service.entity.Order;
import khuong.com.tmbackend.purchase_service.enums.OrderStatus;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    Page<Order> findByUserId(Long userId, Pageable pageable);
    
    List<Order> findByUserIdOrderByOrderDateDesc(Long userId);
    
    // Filter by status
    Page<Order> findByUserIdAndStatus(Long userId, OrderStatus status, Pageable pageable);
    
    // Filter by date range
    Page<Order> findByUserIdAndOrderDateBetween(Long userId, Instant startDate, Instant endDate, Pageable pageable);
    
    // Combined filters
    @Query("SELECT o FROM Order o WHERE " +
           "o.user.id = :userId AND " +
           "(:status IS NULL OR o.status = :status) AND " +
           "(:startDate IS NULL OR o.orderDate >= :startDate) AND " +
           "(:endDate IS NULL OR o.orderDate <= :endDate) AND " +
           "(:paymentMethod IS NULL OR o.paymentMethod = :paymentMethod) AND " +
           "(:paymentStatus IS NULL OR o.paymentStatus = :paymentStatus)")
    Page<Order> filterOrders(
            @Param("userId") Long userId,
            @Param("status") OrderStatus status,
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate,
            @Param("paymentMethod") String paymentMethod,
            @Param("paymentStatus") String paymentStatus,
            Pageable pageable);
} 