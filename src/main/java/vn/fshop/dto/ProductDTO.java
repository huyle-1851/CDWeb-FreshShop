package vn.fshop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.fshop.model.Product;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    private Integer id;
    private String name;
    private String description;
    private Integer price;
    private Integer discountedPrice;
    private String status;
    private Integer quantity;
    private CategoryDTO category;

    // Constructor from Product entity
    public ProductDTO(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.description = product.getDescription();
        this.price = product.getPrice();
        this.discountedPrice = product.getDiscountedPrice();
        this.status = product.getStatus();
        this.quantity = product.getQuantity();
        
        if (product.getCategory() != null) {
            this.category = new CategoryDTO(product.getCategory());
        }
    }
}
