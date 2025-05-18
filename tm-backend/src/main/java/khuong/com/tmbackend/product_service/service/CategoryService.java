package khuong.com.tmbackend.product_service.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import khuong.com.tmbackend.product_service.entity.Category;
import khuong.com.tmbackend.product_service.exception.ResourceNotFoundException;
import khuong.com.tmbackend.product_service.exception.CategoryDeletionException;
import khuong.com.tmbackend.product_service.payload.CategoryDTO;
import khuong.com.tmbackend.product_service.repository.CategoryRepository;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;
    
    public List<CategoryDTO> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        return categories.stream()
                .map(this::mapToCategoryDTO)
                .collect(Collectors.toList());
    }
    
    public CategoryDTO getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
        return mapToCategoryDTO(category);
    }
    
    public List<Object[]> getCategoriesWithProductCount() {
        return categoryRepository.findCategoriesWithProductCount();
    }
    
    @Transactional
    public CategoryDTO createCategory(Category category) {
        Category savedCategory = categoryRepository.save(category);
        return mapToCategoryDTO(savedCategory);
    }
    
    @Transactional
    public CategoryDTO updateCategory(Long id, Category categoryRequest) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
        
        category.setName(categoryRequest.getName());
        category.setDescription(categoryRequest.getDescription());
        
        Category updatedCategory = categoryRepository.save(category);
        return mapToCategoryDTO(updatedCategory);
    }
    
    @Transactional
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
        
        // Check if category has any products using the repository
        Long productCount = categoryRepository.countProductsByCategoryId(id);
        if (productCount > 0) {
            throw new CategoryDeletionException("Cannot delete category with id " + id + " because it contains " + productCount + " products");
        }
        
        categoryRepository.delete(category);
    }
    
    private CategoryDTO mapToCategoryDTO(Category category) {
        return CategoryDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .build();
    }
} 