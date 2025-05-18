package khuong.com.tmbackend.product_service.entity;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(unique = true)
    private String name;

    private String description;

    @OneToMany(mappedBy = "category")
    private Set<Product> products = new HashSet<>();

    // @ManyToOne
    // @JoinColumn(name = "parent_category_id")
    // private Category parentCategory; // For hierarchical categories

    // @OneToMany(mappedBy = "parentCategory")
    // private Set<Category> subCategories = new HashSet<>();

    // Constructors, Getters, Setters (Lombok)
}
