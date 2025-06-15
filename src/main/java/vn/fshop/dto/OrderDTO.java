package vn.fshop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.fshop.model.Order;
import vn.fshop.model.OrderItem;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    private Integer id;
    private LocalDateTime orderDate;
    private String status;
    private Integer totalAmount;
    private String deliveryAddress;
    private String phone;
    private String notes;
    private String paymentMethod;
    private String paymentStatus;
    private UserDTO user;
    private List<OrderItemDTO> orderItems = new ArrayList<>();

    // Constructor from Order entity
    public OrderDTO(Order order) {
        this.id = order.getId();
        this.orderDate = order.getOrderDate();
        this.status = order.getStatus();
        this.totalAmount = order.getTotalAmount();
        this.deliveryAddress = order.getDeliveryAddress();
        this.phone = order.getPhone();
        this.notes = order.getNotes();
        this.paymentMethod = order.getPaymentMethod();
        this.paymentStatus = order.getPaymentStatus();

        if (order.getUser() != null) {
            this.user = new UserDTO(order.getUser());
        }

        // Convert order items to DTOs
        if (order.getOrderItems() != null) {
            for (OrderItem item : order.getOrderItems()) {
                this.orderItems.add(new OrderItemDTO(item));
            }
        }
    }
}
