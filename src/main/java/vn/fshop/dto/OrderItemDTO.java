package vn.fshop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.fshop.model.OrderItem;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDTO {
    private Integer id;
    private Integer quantity;
    private Integer price;
    private Integer totalPrice;
    private ProductDTO product;

    // Constructor from OrderItem entity
    public OrderItemDTO(OrderItem orderItem) {
        this.id = orderItem.getId();
        this.quantity = orderItem.getQuantity();
        this.price = orderItem.getUnitPrice();
        this.totalPrice = orderItem.getTotalPrice();
        
        if (orderItem.getProduct() != null) {
            this.product = new ProductDTO(orderItem.getProduct());
        }
    }
}
