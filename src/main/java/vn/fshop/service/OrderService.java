package vn.fshop.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.fshop.model.Order;
import vn.fshop.model.OrderItem;
import vn.fshop.model.User;
import vn.fshop.model.Product;
import vn.fshop.model.CartItem;
import vn.fshop.repository.OrderRepository;
import vn.fshop.repository.OrderItemRepository;
import vn.fshop.util.CartUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    // Get all orders
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    // Get order by ID
    public Optional<Order> getOrderById(Integer id) {
        return orderRepository.findById(id);
    }

    // Get orders by status
    public List<Order> getOrdersByStatus(String status) {
        return orderRepository.findByStatusOrderByOrderDateDesc(status);
    }

    // Update order status
    public Order updateOrderStatus(Integer orderId, String status) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();
            order.setStatus(status);
            return orderRepository.save(order);
        }
        throw new RuntimeException("Order not found with ID: " + orderId);
    }

    // Update payment status
    public Order updatePaymentStatus(Integer orderId, String paymentStatus) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();
            order.setPaymentStatus(paymentStatus);
            return orderRepository.save(order);
        }
        throw new RuntimeException("Order not found with ID: " + orderId);
    }

    // Get order statistics
    public long getOrderCountByStatus(String status) {
        return orderRepository.countByStatus(status);
    }

    public Long getTotalRevenue() {
        return orderRepository.getTotalRevenue();
    }

    public Long getRevenueByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return orderRepository.getRevenueByDateRange(startDate, endDate);
    }

    // Get recent orders (last 7 days)
    public List<Order> getRecentOrders() {
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        return orderRepository.findRecentOrders(sevenDaysAgo);
    }

    // Create order from cart
    public Order createOrderFromCart(User user, List<CartItem> cartItems, String deliveryAddress,
                                   String phone, String notes) {
        try {
            // Calculate total amount using utility method
            int totalAmount = CartUtils.calculateCartTotalAsInt(cartItems);

            // Create order
            Order order = new Order();
            order.setUser(user);
            order.setTotalAmount(totalAmount);
            order.setDeliveryAddress(deliveryAddress);
            order.setPhone(phone);
            order.setNotes(notes);
            order.setPaymentMethod("COD"); // Cash on Delivery
            order.setStatus("PENDING");
            order.setPaymentStatus("PENDING");
            order.setOrderDate(LocalDateTime.now());

            // Save order first
            order = orderRepository.save(order);

            // Create order items
            for (CartItem cartItem : cartItems) {
                Product product = cartItem.getProduct();
                int unitPrice = (int) CartUtils.getEffectivePrice(product);

                OrderItem orderItem = new OrderItem(order, product, cartItem.getQuantity(), unitPrice);
                orderItemRepository.save(orderItem);
            }

            return order;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create order: " + e.getMessage());
        }
    }

    // Get orders by user
    public List<Order> getOrdersByUser(User user) {
        return orderRepository.findByUserOrderByOrderDateDesc(user);
    }

    // Get order items by order ID
    public List<OrderItem> getOrderItems(Integer orderId) {
        return orderItemRepository.findByOrder_Id(orderId);
    }

    // Save order
    public Order saveOrder(Order order) {
        return orderRepository.save(order);
    }

    // Find order by ID
    public Optional<Order> findById(Integer orderId) {
        return orderRepository.findById(orderId);
    }

    // Delete order
    public void deleteOrder(Integer orderId) {
        orderRepository.deleteById(orderId);
    }
}
