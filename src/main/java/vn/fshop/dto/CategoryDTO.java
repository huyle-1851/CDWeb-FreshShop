package vn.fshop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.fshop.model.Category;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO {
    private Integer id;
    private String name;
    private Integer productCount;

    // Constructor from Category entity
    public CategoryDTO(Category category) {
        this.id = category.getId();
        this.name = category.getName();
    }

    // Constructor with product count
    public CategoryDTO(Category category, Integer productCount) {
        this.id = category.getId();
        this.name = category.getName();
        this.productCount = productCount;
    }
}
