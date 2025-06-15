package vn.fshop.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "product")
@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    @NotBlank(message = "Product name is required")
    private String name;

    @Column(nullable = false)
    @NotNull(message = "Price is required")
    @Min(value = 0, message = "Price must be positive")
    private Integer price;

    @Column(name = "discounted_price")
    @Min(value = 0, message = "Discounted price must be positive")
    private Integer discountedPrice = 0;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    @NotNull(message = "Category is required")
    private Category category;

    @Column(columnDefinition = "TEXT")
    private String description;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Image> images = new ArrayList<>();

    @Column
    private String status = "ACTIVE";

    @Column
    @Min(value = 0, message = "Quantity must be positive")
    private Integer quantity = 0;
}
