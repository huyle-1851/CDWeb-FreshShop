package vn.fshop.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "order_date", nullable = false)
    private LocalDateTime orderDate = LocalDateTime.now();

    @Column(name = "status", nullable = false)
    private String status = "PENDING"; // PENDING, CONFIRMED, SHIPPING, DELIVERED, CANCELLED

    @Column(name = "total_amount", nullable = false)
    private Integer totalAmount = 0;

    @Column(name = "delivery_address")
    private String deliveryAddress;

    @Column(name = "phone")
    private String phone;

    @Column(name = "notes")
    private String notes;

    @Column(name = "payment_method")
    private String paymentMethod = "COD"; // Cash on Delivery

    @Column(name = "payment_status")
    private String paymentStatus = "PENDING"; // PENDING, PAID, FAILED

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<OrderItem> orderItems = new ArrayList<>();

    // Helper methods for status display
    public String getStatusDisplay() {
        switch (status) {
            case "PENDING": return "Chờ xử lý";
            case "CONFIRMED": return "Đã xác nhận";
            case "SHIPPING": return "Đang giao hàng";
            case "DELIVERED": return "Đã giao hàng";
            case "CANCELLED": return "Đã hủy";
            default: return status;
        }
    }

    public String getPaymentStatusDisplay() {
        switch (paymentStatus) {
            case "PENDING": return "Chờ thanh toán";
            case "PAID": return "Đã thanh toán";
            case "FAILED": return "Thanh toán thất bại";
            default: return paymentStatus;
        }
    }
}
