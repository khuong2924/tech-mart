package khuong.com.tmbackend.product_service.service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import khuong.com.tmbackend.product_service.entity.Category;
import khuong.com.tmbackend.product_service.entity.Product;
import khuong.com.tmbackend.product_service.entity.ProductVariant;
import khuong.com.tmbackend.product_service.exception.ResourceNotFoundException;
import khuong.com.tmbackend.product_service.payload.CategoryDTO;
import khuong.com.tmbackend.product_service.payload.CreateProductRequest;
import khuong.com.tmbackend.product_service.payload.PagedResponse;
import khuong.com.tmbackend.product_service.payload.ProductFilterRequest;
import khuong.com.tmbackend.product_service.payload.ProductResponseDTO;
import khuong.com.tmbackend.product_service.payload.ProductVariantDTO;
import khuong.com.tmbackend.product_service.repository.CategoryRepository;
import khuong.com.tmbackend.product_service.repository.ProductRepository;
import khuong.com.tmbackend.product_service.repository.ReviewRepository;

@Service
public class ProductService {
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private ReviewRepository reviewRepository;
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    public PagedResponse<ProductResponseDTO> getAllProducts(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? 
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Product> products = productRepository.findAll(pageable);
        
        return createProductResponse(products);
    }
    
    public ProductResponseDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        
        return mapToProductResponseDTO(product);
    }
    
    public PagedResponse<ProductResponseDTO> getProductsByCategory(Long categoryId, int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? 
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Product> products = productRepository.findByCategoryId(categoryId, pageable);
        
        return createProductResponse(products);
    }
    
    public PagedResponse<ProductResponseDTO> searchProducts(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> products = productRepository.searchByKeyword(keyword, pageable);
        
        return createProductResponse(products);
    }
    
    public PagedResponse<ProductResponseDTO> filterProducts(ProductFilterRequest filterRequest) {
        // Create pageable with sorting
        Sort sort = filterRequest.getSortDirection().equalsIgnoreCase(Sort.Direction.ASC.name()) ? 
                Sort.by(filterRequest.getSortBy()).ascending() : Sort.by(filterRequest.getSortBy()).descending();
        
        Pageable pageable = PageRequest.of(filterRequest.getPage(), filterRequest.getSize(), sort);
        
        // Apply filters
        Page<Product> products = productRepository.filterProducts(
                filterRequest.getCategoryId(),
                filterRequest.getKeyword(),
                filterRequest.getMinPrice(),
                filterRequest.getMaxPrice(),
                filterRequest.getInStock(),
                pageable);
        
        return createProductResponse(products);
    }
    
    @Transactional
    public ProductResponseDTO createProduct(CreateProductRequest request) {
        // Create new product
        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());
        product.setImageUrl(request.getImageUrl());
        
        // Load and set category
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));
            product.setCategory(category);
        }
        
        Product savedProduct = productRepository.save(product);
        return mapToProductResponseDTO(savedProduct);
    }
    
    @Transactional
    public ProductResponseDTO updateProduct(Long id, Product productRequest) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        
        // Update product fields
        product.setName(productRequest.getName());
        product.setDescription(productRequest.getDescription());
        product.setPrice(productRequest.getPrice());
        product.setStockQuantity(productRequest.getStockQuantity());
        product.setCategory(productRequest.getCategory());
        product.setImageUrl(productRequest.getImageUrl());
        product.setActive(productRequest.isActive());
        product.setDiscountPercentage(productRequest.getDiscountPercentage());
        
        Product updatedProduct = productRepository.save(product);
        return mapToProductResponseDTO(updatedProduct);
    }
    
    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        
        productRepository.delete(product);
    }
    
    private PagedResponse<ProductResponseDTO> createProductResponse(Page<Product> productPage) {
        List<Product> products = productPage.getNumberOfElements() == 0 ? 
                Collections.emptyList() : productPage.getContent();
        
        List<ProductResponseDTO> content = products.stream()
                .map(this::mapToProductResponseDTO)
                .collect(Collectors.toList());
        
        return new PagedResponse<>(
                content, 
                productPage.getNumber(), 
                productPage.getSize(), 
                productPage.getTotalElements(), 
                productPage.getTotalPages(), 
                productPage.isLast());
    }
    
    private ProductResponseDTO mapToProductResponseDTO(Product product) {
        // Get rating info
        Double averageRating = reviewRepository.findAverageRatingByProductId(product.getId());
        Integer reviewCount = reviewRepository.countByProductId(product.getId());
        
        // Map the variants
        List<ProductVariantDTO> variantDTOs = product.getVariants().stream()
                .map(this::mapToProductVariantDTO)
                .collect(Collectors.toList());
        
        // Map category
        CategoryDTO categoryDTO = null;
        if (product.getCategory() != null) {
            categoryDTO = CategoryDTO.builder()
                    .id(product.getCategory().getId())
                    .name(product.getCategory().getName())
                    .description(product.getCategory().getDescription())
                    .build();
        }
        
        return ProductResponseDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stockQuantity(product.getStockQuantity())
                .category(categoryDTO)
                .variants(variantDTOs)
                .imageUrl(product.getImageUrl())
                .active(product.isActive())
                .discountPercentage(product.getDiscountPercentage())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .averageRating(averageRating)
                .reviewCount(reviewCount != null ? reviewCount : 0)
                .build();
    }
    
    private ProductVariantDTO mapToProductVariantDTO(ProductVariant variant) {
        return ProductVariantDTO.builder()
                .id(variant.getId())
                .name(variant.getName())
                .sku(variant.getSku())
                .priceAdjustment(variant.getPriceAdjustment())
                .stockQuantity(variant.getStockQuantity())
                .attributes(variant.getAttributes())
                .imageUrl(variant.getImageUrl())
                .finalPrice(variant.getProduct().getPrice().add(variant.getPriceAdjustment()))
                .build();
    }
} 