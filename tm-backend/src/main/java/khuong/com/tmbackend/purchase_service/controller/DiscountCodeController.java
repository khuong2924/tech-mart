package khuong.com.tmbackend.purchase_service.controller;

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
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DiscountCodeDTO> getDiscountCodeById(@PathVariable Long id) {
        DiscountCodeDTO code = discountCodeService.getDiscountCodeById(id);
        return ResponseEntity.ok(code);
    }
    
    @GetMapping("/by-code")
    @PreAuthorize("hasRole('ADMIN')")
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
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DiscountCodeDTO> createDiscountCode(@Valid @RequestBody DiscountCode code) {
        DiscountCodeDTO newCode = discountCodeService.createDiscountCode(code);
        return new ResponseEntity<>(newCode, HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DiscountCodeDTO> updateDiscountCode(
            @PathVariable Long id, 
            @Valid @RequestBody DiscountCode code) {
        
        DiscountCodeDTO updatedCode = discountCodeService.updateDiscountCode(id, code);
        return ResponseEntity.ok(updatedCode);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteDiscountCode(@PathVariable Long id) {
        discountCodeService.deleteDiscountCode(id);
        return ResponseEntity.noContent().build();
    }
    
    @PutMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DiscountCodeDTO> deactivateDiscountCode(@PathVariable Long id) {
        DiscountCodeDTO deactivatedCode = discountCodeService.deactivateDiscountCode(id);
        return ResponseEntity.ok(deactivatedCode);
    }
} 