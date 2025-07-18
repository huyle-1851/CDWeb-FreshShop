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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    // Update payment status with revenue tracking
    public Order updatePaymentStatus(Integer orderId, String paymentStatus) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();
            String oldPaymentStatus = order.getPaymentStatus();

            // Update payment status
            order.setPaymentStatus(paymentStatus);
            Order savedOrder = orderRepository.save(order);

            // Log revenue change for tracking purposes
            logRevenueChange(order, oldPaymentStatus, paymentStatus);

            return savedOrder;
        }
        throw new RuntimeException("Order not found with ID: " + orderId);
    }

    // Helper method to log revenue changes
    private void logRevenueChange(Order order, String oldStatus, String newStatus) {
        boolean wasRevenueCounted = "PAID".equals(oldStatus);
        boolean isRevenueCounted = "PAID".equals(newStatus);

        // Revenue tracking logic without console output
        if (!wasRevenueCounted && isRevenueCounted) {
            // Revenue added
        } else if (wasRevenueCounted && !isRevenueCounted) {
            // Revenue removed
        }
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

    // Get count of paid orders
    public long getPaidOrdersCount() {
        return orderRepository.countByPaymentStatus("PAID");
    }

    // Get count of pending payment orders
    public long getPendingPaymentOrdersCount() {
        return orderRepository.countByPaymentStatus("PENDING");
    }

    // Get revenue statistics summary
    public Map<String, Object> getRevenueStatistics() {
        Long totalRevenue = getTotalRevenue();
        long paidOrdersCount = getPaidOrdersCount();
        long pendingPaymentCount = getPendingPaymentOrdersCount();

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalRevenue", totalRevenue != null ? totalRevenue : 0);
        stats.put("paidOrdersCount", paidOrdersCount);
        stats.put("pendingPaymentCount", pendingPaymentCount);
        stats.put("averageOrderValue", paidOrdersCount > 0 ? (totalRevenue != null ? totalRevenue : 0) / paidOrdersCount : 0);

        return stats;
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

    // Cancel order (change status to CANCELLED)
    public Order cancelOrder(Integer orderId) {
        try {
            Optional<Order> orderOpt = orderRepository.findById(orderId);
            if (!orderOpt.isPresent()) {
                throw new RuntimeException("Order not found with ID: " + orderId);
            }

            Order order = orderOpt.get();

            // Check if order can be cancelled
            if ("DELIVERED".equals(order.getStatus()) || "CANCELLED".equals(order.getStatus())) {
                throw new RuntimeException("Cannot cancel order with status: " + order.getStatus());
            }

            // Cancel the order
            order.setStatus("CANCELLED");

            // Save the order
            Order savedOrder = orderRepository.save(order);

            return savedOrder;

        } catch (Exception e) {
            System.out.println("Error cancelling order: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to cancel order: " + e.getMessage(), e);
        }
    }

    // Delete order (with proper handling of foreign key constraints)
    // Only allows deletion of CANCELLED orders
    public void deleteOrder(Integer orderId) {
        try {
            // First, find the order to ensure it exists
            Optional<Order> orderOpt = orderRepository.findById(orderId);
            if (!orderOpt.isPresent()) {
                throw new RuntimeException("Order not found with ID: " + orderId);
            }

            Order order = orderOpt.get();

            // Only allow deletion of cancelled orders
            if (!"CANCELLED".equals(order.getStatus())) {
                throw new RuntimeException("Only cancelled orders can be deleted. Current status: " + order.getStatus());
            }

            // Delete all order items first to avoid foreign key constraint issues
            List<OrderItem> orderItems = orderItemRepository.findByOrder_Id(orderId);
            if (!orderItems.isEmpty()) {
                orderItemRepository.deleteAll(orderItems);
            }

            // Now delete the order
            orderRepository.delete(order);

        } catch (Exception e) {
            throw new RuntimeException("Failed to delete order: " + e.getMessage(), e);
        }
    }
}
