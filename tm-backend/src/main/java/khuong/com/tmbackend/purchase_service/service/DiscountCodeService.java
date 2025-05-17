package khuong.com.tmbackend.purchase_service.service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import khuong.com.tmbackend.purchase_service.dto.DiscountCodeDTO;
import khuong.com.tmbackend.purchase_service.entity.DiscountCode;
import khuong.com.tmbackend.purchase_service.exception.ResourceNotFoundException;
import khuong.com.tmbackend.purchase_service.repository.DiscountCodeRepository;

@Service
public class DiscountCodeService {
    
    @Autowired
    private DiscountCodeRepository discountCodeRepository;
    
    public List<DiscountCodeDTO> getAllActiveCodes() {
        List<DiscountCode> codes = discountCodeRepository.findByActiveTrueAndValidToAfter(Instant.now());
        return codes.stream().map(this::mapToDiscountCodeDTO).collect(Collectors.toList());
    }
    
    public DiscountCodeDTO getDiscountCodeById(Long id) {
        DiscountCode code = discountCodeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Discount code not found with id: " + id));
        return mapToDiscountCodeDTO(code);
    }
    
    public DiscountCodeDTO getDiscountCodeByCode(String code) {
        DiscountCode discountCode = discountCodeRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Discount code not found: " + code));
        return mapToDiscountCodeDTO(discountCode);
    }
    
    public DiscountCodeDTO validateDiscountCode(String code) {
        DiscountCode discountCode = discountCodeRepository.findValidDiscountCode(code, Instant.now())
                .orElseThrow(() -> new IllegalArgumentException("Invalid or expired discount code"));
        return mapToDiscountCodeDTO(discountCode);
    }
    
    @Transactional
    public DiscountCodeDTO createDiscountCode(DiscountCode discountCode) {
        // Set default values if not provided
        if (discountCode.getValidFrom() == null) {
            discountCode.setValidFrom(Instant.now());
        }
        
        if (discountCode.getUsageCount() == null) {
            discountCode.setUsageCount(0);
        }
        
        discountCode.setActive(true);
        
        DiscountCode savedCode = discountCodeRepository.save(discountCode);
        return mapToDiscountCodeDTO(savedCode);
    }
    
    @Transactional
    public DiscountCodeDTO updateDiscountCode(Long id, DiscountCode codeRequest) {
        DiscountCode code = discountCodeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Discount code not found with id: " + id));
        
        // Update fields
        if (codeRequest.getCode() != null) {
            code.setCode(codeRequest.getCode());
        }
        
        if (codeRequest.getDescription() != null) {
            code.setDescription(codeRequest.getDescription());
        }
        
        if (codeRequest.getDiscountAmount() != null) {
            code.setDiscountAmount(codeRequest.getDiscountAmount());
        }
        
        if (codeRequest.getDiscountPercentage() != null) {
            code.setDiscountPercentage(codeRequest.getDiscountPercentage());
        }
        
        if (codeRequest.getMinimumOrderAmount() != null) {
            code.setMinimumOrderAmount(codeRequest.getMinimumOrderAmount());
        }
        
        if (codeRequest.getMaximumDiscountAmount() != null) {
            code.setMaximumDiscountAmount(codeRequest.getMaximumDiscountAmount());
        }
        
        if (codeRequest.getValidFrom() != null) {
            code.setValidFrom(codeRequest.getValidFrom());
        }
        
        if (codeRequest.getValidTo() != null) {
            code.setValidTo(codeRequest.getValidTo());
        }
        
        if (codeRequest.getUsageLimit() != null) {
            code.setUsageLimit(codeRequest.getUsageLimit());
        }
        
        code.setActive(codeRequest.isActive());
        
        DiscountCode updatedCode = discountCodeRepository.save(code);
        return mapToDiscountCodeDTO(updatedCode);
    }
    
    @Transactional
    public void deleteDiscountCode(Long id) {
        DiscountCode code = discountCodeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Discount code not found with id: " + id));
        
        discountCodeRepository.delete(code);
    }
    
    @Transactional
    public DiscountCodeDTO deactivateDiscountCode(Long id) {
        DiscountCode code = discountCodeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Discount code not found with id: " + id));
        
        code.setActive(false);
        DiscountCode updatedCode = discountCodeRepository.save(code);
        return mapToDiscountCodeDTO(updatedCode);
    }
    
    private DiscountCodeDTO mapToDiscountCodeDTO(DiscountCode code) {
        return DiscountCodeDTO.builder()
                .id(code.getId())
                .code(code.getCode())
                .description(code.getDescription())
                .discountAmount(code.getDiscountAmount())
                .discountPercentage(code.getDiscountPercentage())
                .minimumOrderAmount(code.getMinimumOrderAmount())
                .maximumDiscountAmount(code.getMaximumDiscountAmount())
                .validFrom(code.getValidFrom())
                .validTo(code.getValidTo())
                .usageLimit(code.getUsageLimit())
                .usageCount(code.getUsageCount())
                .active(code.isActive())
                .build();
    }
} 