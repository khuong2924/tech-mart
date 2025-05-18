package khuong.com.tmbackend.product_service.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import khuong.com.tmbackend.product_service.entity.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    
    Optional<Category> findByName(String name);
    
    // Find categories with product counts
    @Query("SELECT c, COUNT(p) FROM Category c LEFT JOIN Product p ON p.category.id = c.id " +
           "GROUP BY c.id ORDER BY c.name ASC")
    List<Object[]> findCategoriesWithProductCount();
    
    @Query("SELECT COUNT(p) FROM Product p WHERE p.category.id = :categoryId")
    Long countProductsByCategoryId(@Param("categoryId") Long categoryId);
} 