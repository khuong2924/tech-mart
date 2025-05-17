package khuong.com.tmbackend.purchase_service.dto;

import java.time.Instant;

import khuong.com.tmbackend.purchase_service.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderFilterRequest {
    // Filter criteria
    private OrderStatus status;
    private Instant startDate;
    private Instant endDate;
    private String paymentMethod;
    private String paymentStatus;
    
    // Sorting criteria
    private String sortBy = "orderDate"; // Default sort by orderDate
    private String sortDirection = "desc"; // asc or desc, default to newest first
    
    // Pagination parameters
    private int page = 0;
    private int size = 10;
} 