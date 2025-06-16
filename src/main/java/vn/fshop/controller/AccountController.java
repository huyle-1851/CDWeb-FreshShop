package vn.fshop.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import vn.fshop.dto.OrderDTO;
import vn.fshop.model.User;
import vn.fshop.model.Order;
import vn.fshop.service.UserService;
import vn.fshop.service.OrderService;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

@Controller
@RequestMapping("/account")
public class AccountController {

    @Autowired
    private UserService userService;

    @Autowired
    private OrderService orderService;

    // Update user profile via AJAX
    @PostMapping("/profile/update")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateProfile(
            @RequestParam String firstname,
            @RequestParam String lastname,
            @RequestParam String phone,
            @RequestParam String email,
            @RequestParam String address,
            HttpSession session) {
        
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }

        User user = userService.findByUsername(username).orElse(null);
        if (user == null) {
            return ResponseEntity.status(404).body(Map.of("error", "User not found"));
        }

        try {
            // Validate input
            Map<String, String> errors = new HashMap<>();
            
            if (firstname == null || firstname.trim().isEmpty()) {
                errors.put("firstname", "Tên không được để trống");
            }
            if (lastname == null || lastname.trim().isEmpty()) {
                errors.put("lastname", "Họ không được để trống");
            }
            if (phone == null || !phone.matches("\\d{10}")) {
                errors.put("phone", "Số điện thoại phải có 10 chữ số");
            }
            if (email == null || !email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$")) {
                errors.put("email", "Email không hợp lệ");
            }
            
            // Check if email is already used by another user
            if (!user.getEmail().equals(email) && userService.existsByEmail(email)) {
                errors.put("email", "Email đã được sử dụng");
            }

            if (!errors.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("errors", errors));
            }

            // Update user
            user.setFirstname(firstname.trim());
            user.setLastname(lastname.trim());
            user.setPhone(phone.trim());
            user.setEmail(email.trim());
            user.setAddress(address != null ? address.trim() : "");

            User updatedUser = userService.saveUser(user);

            Map<String, Object> response = new HashMap<>();
            response.put("user", updatedUser);
            response.put("message", "Cập nhật thông tin thành công");
            response.put("success", true);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Có lỗi xảy ra: " + e.getMessage()));
        }
    }

    // Change password via AJAX
    @PostMapping("/password/change")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> changePassword(
            @RequestParam String currentPassword,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword,
            HttpSession session) {
        
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }

        User user = userService.findByUsername(username).orElse(null);
        if (user == null) {
            return ResponseEntity.status(404).body(Map.of("error", "User not found"));
        }

        try {
            // Validate passwords
            Map<String, String> errors = new HashMap<>();
            
            if (currentPassword == null || currentPassword.trim().isEmpty()) {
                errors.put("currentPassword", "Mật khẩu hiện tại không được để trống");
            }
            if (newPassword == null || newPassword.length() < 6) {
                errors.put("newPassword", "Mật khẩu mới phải có ít nhất 6 ký tự");
            }
            if (!newPassword.equals(confirmPassword)) {
                errors.put("confirmPassword", "Xác nhận mật khẩu không khớp");
            }

            if (!errors.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("errors", errors));
            }

            // Verify current password
            if (!user.getPassword().equals(currentPassword)) {
                return ResponseEntity.badRequest().body(Map.of("error", "Mật khẩu hiện tại không đúng"));
            }

            // Update password
            user.setPassword(newPassword);
            userService.saveUser(user);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Đổi mật khẩu thành công");
            response.put("success", true);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Có lỗi xảy ra: " + e.getMessage()));
        }
    }

    // Get user orders
    @GetMapping("/orders")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getUserOrders(HttpSession session) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }

        try {
            User user = userService.findByUsername(username).orElse(null);
            if (user == null) {
                return ResponseEntity.status(401).body(Map.of("error", "User not found"));
            }

            List<Order> orders = orderService.getOrdersByUser(user);

            // Convert to DTOs to avoid lazy loading issues
            List<OrderDTO> orderDTOs = orders.stream()
                    .map(OrderDTO::new)
                    .collect(java.util.stream.Collectors.toList());

            // Calculate statistics
            int orderCount = orders.size();
            long totalSpent = orders.stream()
                    .filter(order -> "PAID".equals(order.getPaymentStatus()))
                    .mapToLong(Order::getTotalAmount)
                    .sum();

            Map<String, Object> response = new HashMap<>();
            response.put("orders", orderDTOs);
            response.put("orderCount", orderCount);
            response.put("totalSpent", totalSpent);
            response.put("success", true);

            if (orders.isEmpty()) {
                response.put("message", "Bạn chưa có đơn hàng nào");
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to load orders: " + e.getMessage()));
        }
    }

    // Cancel user order
    @PostMapping("/orders/{orderId}/cancel")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> cancelOrder(@PathVariable Integer orderId, HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        try {
            String username = (String) session.getAttribute("username");
            if (username == null) {
                response.put("success", false);
                response.put("error", "Phiên đăng nhập đã hết hạn");
                return ResponseEntity.status(401).body(response);
            }

            User user = userService.findByUsername(username).orElse(null);
            if (user == null) {
                response.put("success", false);
                response.put("error", "Không tìm thấy người dùng");
                return ResponseEntity.status(401).body(response);
            }

            Order order = orderService.findById(orderId).orElse(null);
            if (order == null) {
                response.put("success", false);
                response.put("error", "Không tìm thấy đơn hàng với ID: " + orderId);
                return ResponseEntity.badRequest().body(response);
            }

            // Check if user owns this order
            if (!order.getUser().getId().equals(user.getId())) {
                response.put("success", false);
                response.put("error", "Bạn không có quyền hủy đơn hàng này");
                return ResponseEntity.status(403).body(response);
            }

            // Check if order can be cancelled
            if (!"PENDING".equals(order.getStatus()) && !"CONFIRMED".equals(order.getStatus())) {
                response.put("success", false);
                response.put("error", "Không thể hủy đơn hàng ở trạng thái: " + order.getStatus());
                return ResponseEntity.badRequest().body(response);
            }

            // Cancel the order
            order.setStatus("CANCELLED");
            Order savedOrder = orderService.saveOrder(order);

            response.put("success", true);
            response.put("message", "Hủy đơn hàng #" + orderId + " thành công");
            response.put("order", new OrderDTO(savedOrder));

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace(); // Log the full stack trace
            response.put("success", false);
            response.put("error", "Có lỗi xảy ra khi hủy đơn hàng: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }



    // Get order details
    @GetMapping("/orders/{orderId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getOrderDetails(@PathVariable Integer orderId, HttpSession session) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }

        try {
            User user = userService.findByUsername(username).orElse(null);
            if (user == null) {
                return ResponseEntity.status(401).body(Map.of("error", "User not found"));
            }

            Order order = orderService.findById(orderId).orElse(null);
            if (order == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Không tìm thấy đơn hàng"));
            }

            // Check if user owns this order
            if (!order.getUser().getId().equals(user.getId())) {
                return ResponseEntity.status(403).body(Map.of("error", "Bạn không có quyền xem đơn hàng này"));
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("order", new OrderDTO(order));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Có lỗi xảy ra: " + e.getMessage()));
        }
    }




}
