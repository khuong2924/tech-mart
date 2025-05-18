package khuong.com.tmbackend.purchase_service.controller;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
import khuong.com.tmbackend.purchase_service.dto.CreateDiscountCodeRequest;
import khuong.com.tmbackend.purchase_service.dto.DiscountCodeDTO;
import khuong.com.tmbackend.purchase_service.entity.DiscountCode;
import khuong.com.tmbackend.purchase_service.service.DiscountCodeService;

@RestController
@RequestMapping("/api/discount-codes")
public class DiscountCodeController {
    
    @Autowired
    private DiscountCodeService discountCodeService;
    
    @GetMapping
    public ResponseEntity<List<DiscountCodeDTO>> getAllActiveCodes() {
        List<DiscountCodeDTO> codes = discountCodeService.getAllActiveCodes();
        return ResponseEntity.ok(codes);
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<DiscountCodeDTO> getDiscountCodeById(@PathVariable Long id) {
        DiscountCodeDTO code = discountCodeService.getDiscountCodeById(id);
        return ResponseEntity.ok(code);
    }
    
    @GetMapping("/by-code")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<DiscountCodeDTO> getDiscountCodeByCode(@RequestParam String code) {
        DiscountCodeDTO discountCode = discountCodeService.getDiscountCodeByCode(code);
        return ResponseEntity.ok(discountCode);
    }
    
    @PostMapping("/validate")
    public ResponseEntity<DiscountCodeDTO> validateDiscountCode(@RequestParam String code) {
        try {
            DiscountCodeDTO validCode = discountCodeService.validateDiscountCode(code);
            return ResponseEntity.ok(validCode);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<DiscountCodeDTO> createDiscountCode(@Valid @RequestBody CreateDiscountCodeRequest request) {
        try {
            // Convert CreateDiscountCodeRequest to DiscountCode
            DiscountCode discountCode = new DiscountCode();
            discountCode.setCode(request.getCode());
            discountCode.setDescription(request.getDescription());
            
            // Set discount percentage
            if (request.getDiscountPercent() != null) {
                discountCode.setDiscountPercentage(request.getDiscountPercent());
            }
            
            // Set minimum order amount
            if (request.getMinOrderValue() != null) {
                discountCode.setMinimumOrderAmount(request.getMinOrderValue());
            }
            
            // Set maximum discount amount
            if (request.getMaxDiscount() != null) {
                discountCode.setMaximumDiscountAmount(request.getMaxDiscount());
            }
            
            // Convert date strings to Instant
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            
            if (request.getStartDate() != null && !request.getStartDate().isEmpty()) {
                LocalDate startLocalDate = LocalDate.parse(request.getStartDate(), formatter);
                discountCode.setValidFrom(startLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
            } else {
                discountCode.setValidFrom(Instant.now());
            }
            
            if (request.getEndDate() != null && !request.getEndDate().isEmpty()) {
                LocalDate endLocalDate = LocalDate.parse(request.getEndDate(), formatter);
                discountCode.setValidTo(endLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
            }
            
            discountCode.setUsageLimit(request.getUsageLimit());
            discountCode.setActive(request.isActive());
            discountCode.setUsageCount(0);
            
            DiscountCodeDTO newCode = discountCodeService.createDiscountCode(discountCode);
            return new ResponseEntity<>(newCode, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<DiscountCodeDTO> updateDiscountCode(
            @PathVariable Long id, 
            @Valid @RequestBody CreateDiscountCodeRequest request) {
        try {
            // Convert CreateDiscountCodeRequest to DiscountCode similar to createDiscountCode
            DiscountCode discountCode = new DiscountCode();
            discountCode.setCode(request.getCode());
            discountCode.setDescription(request.getDescription());
            
            if (request.getDiscountPercent() != null) {
                discountCode.setDiscountPercentage(request.getDiscountPercent());
            }
            
            if (request.getMinOrderValue() != null) {
                discountCode.setMinimumOrderAmount(request.getMinOrderValue());
            }
            
            if (request.getMaxDiscount() != null) {
                discountCode.setMaximumDiscountAmount(request.getMaxDiscount());
            }
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            
            if (request.getStartDate() != null && !request.getStartDate().isEmpty()) {
                LocalDate startLocalDate = LocalDate.parse(request.getStartDate(), formatter);
                discountCode.setValidFrom(startLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
            }
            
            if (request.getEndDate() != null && !request.getEndDate().isEmpty()) {
                LocalDate endLocalDate = LocalDate.parse(request.getEndDate(), formatter);
                discountCode.setValidTo(endLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
            }
            
            discountCode.setUsageLimit(request.getUsageLimit());
            discountCode.setActive(request.isActive());
            
            DiscountCodeDTO updatedCode = discountCodeService.updateDiscountCode(id, discountCode);
            return ResponseEntity.ok(updatedCode);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteDiscountCode(@PathVariable Long id) {
        try {
            discountCodeService.deleteDiscountCode(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}/deactivate")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<DiscountCodeDTO> deactivateDiscountCode(@PathVariable Long id) {
        try {
            DiscountCodeDTO deactivatedCode = discountCodeService.deactivateDiscountCode(id);
            return ResponseEntity.ok(deactivatedCode);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
} 