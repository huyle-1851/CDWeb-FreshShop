package vn.fshop.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.fshop.dto.CategoryDTO;
import vn.fshop.dto.OrderDTO;
import vn.fshop.dto.ProductDTO;
import vn.fshop.model.*;
import vn.fshop.service.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminController extends BaseController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private OrderService orderService;

    // ============ DASHBOARD ============
    @GetMapping("/dashboard")
    public String adminDashboard(HttpSession session, Model model) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        model.addAttribute("users", userService.getAllUsers());
        return "admin/dashboard";
    }

    // ============ PRODUCTS MANAGEMENT ============
    @GetMapping("/products")
    public String productManagement(Model model, HttpSession session) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        try {
            List<Category> categories = categoryService.getAllCategory();
            if (categories.isEmpty()) {
                categoryService.createCategory("Rau củ quả");
                categoryService.createCategory("Trái cây");
                categoryService.createCategory("Thịt cá");
                categoryService.createCategory("Đồ uống");
                categories = categoryService.getAllCategory();
            }
            model.addAttribute("currentPage", "admin-products");
            model.addAttribute("categories", categories);
            return "admin/products";
        } catch (Exception e) {
            model.addAttribute("currentPage", "admin-products");
            model.addAttribute("categories", new java.util.ArrayList<>());
            return "admin/products";
        }
    }

    @GetMapping("/products/list")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getAllProducts(HttpSession session) {
        if (!isAdmin(session)) {
            return ResponseEntity.status(403).body(Map.of("error", "Access denied"));
        }
        try {
            List<Product> products = productService.getAllProduct();
            List<ProductDTO> productDTOs = products.stream().map(ProductDTO::new).toList();
            Map<String, Object> response = new HashMap<>();
            response.put("products", productDTOs);
            response.put("success", true);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to load products: " + e.getMessage()));
        }
    }

    @GetMapping("/products/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getProduct(@PathVariable Integer id, HttpSession session) {
        if (!isAdmin(session)) {
            return ResponseEntity.status(403).body(Map.of("error", "Access denied"));
        }
        try {
            Product product = productService.getProductById(id);
            if (product == null) {
                return ResponseEntity.status(404).body(Map.of("error", "Product not found"));
            }
            Map<String, Object> response = new HashMap<>();
            response.put("product", new ProductDTO(product));
            response.put("success", true);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to load product: " + e.getMessage()));
        }
    }

    @PostMapping("/products/add")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> addProduct(
            @RequestParam String name, @RequestParam String description, @RequestParam double price,
            @RequestParam(required = false, defaultValue = "0") double discountedPrice,
            @RequestParam Integer categoryId, @RequestParam(required = false, defaultValue = "true") boolean active,
            @RequestParam(required = false) MultipartFile image, HttpSession session) {
        if (!isAdmin(session)) {
            return ResponseEntity.status(403).body(Map.of("error", "Access denied"));
        }
        try {
            Map<String, String> errors = validateProductInput(name, description, price, categoryId);
            if (!errors.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("errors", errors));
            }

            // Use the new method that handles image upload
            Product product = productService.createProductWithImage(name, description, price, discountedPrice, categoryId, active, image);

            Map<String, Object> response = new HashMap<>();
            response.put("product", product);
            response.put("message", "Sản phẩm đã được thêm thành công" + (image != null && !image.isEmpty() ? " với hình ảnh" : ""));
            response.put("success", true);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to add product: " + e.getMessage()));
        }
    }

    @PostMapping("/products/{id}/update")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateProduct(
            @PathVariable Integer id, @RequestParam String name, @RequestParam String description,
            @RequestParam double price, @RequestParam(required = false, defaultValue = "0") double discountedPrice,
            @RequestParam Integer categoryId, @RequestParam(required = false, defaultValue = "true") boolean active,
            @RequestParam(required = false) MultipartFile image, HttpSession session) {
        if (!isAdmin(session)) {
            return ResponseEntity.status(403).body(Map.of("error", "Access denied"));
        }
        try {
            Map<String, String> errors = validateProductInput(name, description, price, categoryId);
            if (!errors.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("errors", errors));
            }

            // Use the new method that handles image upload
            Product product = productService.updateProductWithImage(id, name, description, price, discountedPrice, categoryId, active, image);

            Map<String, Object> response = new HashMap<>();
            response.put("product", product);
            response.put("message", "Sản phẩm đã được cập nhật thành công" + (image != null && !image.isEmpty() ? " với hình ảnh mới" : ""));
            response.put("success", true);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to update product: " + e.getMessage()));
        }
    }

    @DeleteMapping("/products/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteProduct(@PathVariable Integer id, HttpSession session) {
        if (!isAdmin(session)) {
            return ResponseEntity.status(403).body(Map.of("error", "Access denied"));
        }
        try {
            productService.deleteProduct(id);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Sản phẩm đã được xóa thành công");
            response.put("success", true);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to delete product: " + e.getMessage()));
        }
    }

    @PostMapping("/products/{id}/toggle-status")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> toggleProductStatus(@PathVariable Integer id, HttpSession session) {
        if (!isAdmin(session)) {
            return ResponseEntity.status(403).body(Map.of("error", "Access denied"));
        }
        try {
            Product product = productService.toggleProductStatus(id);
            Map<String, Object> response = new HashMap<>();
            response.put("product", product);
            response.put("message", "Trạng thái sản phẩm đã được cập nhật thành: " + product.getStatusDisplay());
            response.put("success", true);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to toggle status: " + e.getMessage()));
        }
    }

    @PostMapping("/products/{id}/update-status")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateProductStatus(@PathVariable Integer id, @RequestParam String status, HttpSession session) {
        if (!isAdmin(session)) {
            return ResponseEntity.status(403).body(Map.of("error", "Access denied"));
        }
        try {
            Product product = productService.updateProductStatus(id, status);
            Map<String, Object> response = new HashMap<>();
            response.put("product", product);
            response.put("message", "Trạng thái sản phẩm đã được cập nhật thành: " + product.getStatusDisplay());
            response.put("success", true);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to update status: " + e.getMessage()));
        }
    }

    // ============ CATEGORIES MANAGEMENT ============
    @GetMapping("/categories")
    public String categoryManagement(Model model, HttpSession session) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        model.addAttribute("currentPage", "admin-categories");
        return "admin/categories";
    }

    @GetMapping("/categories/list")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getAllCategories(HttpSession session) {
        if (!isAdmin(session)) {
            return ResponseEntity.status(403).body(Map.of("error", "Access denied"));
        }
        try {
            List<Category> categories = categoryService.getAllCategory();
            Map<String, Object> response = new HashMap<>();
            response.put("categories", categories);
            response.put("success", true);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to load categories: " + e.getMessage()));
        }
    }

    @PostMapping("/categories/add")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> addCategory(
            @RequestParam String name, @RequestParam(required = false) String description, HttpSession session) {
        if (!isAdmin(session)) {
            return ResponseEntity.status(403).body(Map.of("error", "Access denied"));
        }
        try {
            if (name == null || name.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Tên danh mục không được để trống"));
            }
            if (categoryService.existsByName(name.trim())) {
                return ResponseEntity.badRequest().body(Map.of("error", "Danh mục đã tồn tại"));
            }
            Category category = categoryService.createCategory(name.trim());
            Map<String, Object> response = new HashMap<>();
            response.put("category", category);
            response.put("message", "Danh mục đã được thêm thành công");
            response.put("success", true);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to add category: " + e.getMessage()));
        }
    }
    @PostMapping("/categories/{id}/update")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateCategory(
            @PathVariable Integer id, @RequestParam String name,
            @RequestParam(required = false) String description, HttpSession session) {
        if (!isAdmin(session)) {
            return ResponseEntity.status(403).body(Map.of("error", "Access denied"));
        }
        try {
            if (name == null || name.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Tên danh mục không được để trống"));
            }
            Category category = categoryService.updateCategory(id, name.trim());
            Map<String, Object> response = new HashMap<>();
            response.put("category", category);
            response.put("message", "Danh mục đã được cập nhật thành công");
            response.put("success", true);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to update category: " + e.getMessage()));
        }
    }

    @DeleteMapping("/categories/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteCategory(@PathVariable Integer id, HttpSession session) {
        if (!isAdmin(session)) {
            return ResponseEntity.status(403).body(Map.of("error", "Access denied"));
        }
        try {
            categoryService.deleteCategory(id);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Danh mục đã được xóa thành công");
            response.put("success", true);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to delete category: " + e.getMessage()));
        }
    }

    @GetMapping("/categories/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getCategory(@PathVariable Integer id, HttpSession session) {
        if (!isAdmin(session)) {
            return ResponseEntity.status(403).body(Map.of("error", "Access denied"));
        }
        try {
            Category category = categoryService.getCategoryById(id);
            if (category == null) {
                return ResponseEntity.status(404).body(Map.of("error", "Category not found"));
            }
            Map<String, Object> response = new HashMap<>();
            response.put("category", category);
            response.put("success", true);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to load category: " + e.getMessage()));
        }
    }

    // ============ ORDERS MANAGEMENT ============
    @GetMapping("/orders")
    public String orderManagement(Model model, HttpSession session) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        model.addAttribute("currentPage", "admin-orders");
        return "admin/orders";
    }

    @GetMapping("/orders/list")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getAllOrders(HttpSession session) {
        if (!isAdmin(session)) {
            return ResponseEntity.status(403).body(Map.of("error", "Access denied"));
        }
        try {
            List<Order> orders = orderService.getAllOrders();
            List<OrderDTO> orderDTOs = orders.stream().map(OrderDTO::new).toList();
            long pendingCount = orderService.getOrderCountByStatus("PENDING");
            long confirmedCount = orderService.getOrderCountByStatus("CONFIRMED");
            long shippingCount = orderService.getOrderCountByStatus("SHIPPING");
            long deliveredCount = orderService.getOrderCountByStatus("DELIVERED");
            long paidCount = orderService.getPaidOrdersCount();
            long pendingPaymentCount = orderService.getPendingPaymentOrdersCount();
            Long totalRevenue = orderService.getTotalRevenue();
            Map<String, Object> response = new HashMap<>();
            response.put("orders", orderDTOs);
            response.put("statistics", Map.of(
                "pending", pendingCount, "confirmed", confirmedCount,
                "shipping", shippingCount, "delivered", deliveredCount,
                "paid", paidCount, "pendingPayment", pendingPaymentCount,
                "totalRevenue", totalRevenue != null ? totalRevenue : 0
            ));
            response.put("success", true);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to load orders: " + e.getMessage()));
        }
    }

    @PostMapping("/orders/{id}/status")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateOrderStatus(
            @PathVariable Integer id, @RequestParam String status, HttpSession session) {
        if (!isAdmin(session)) {
            return ResponseEntity.status(403).body(Map.of("error", "Access denied"));
        }
        try {
            Order order = orderService.updateOrderStatus(id, status);
            Map<String, Object> response = new HashMap<>();
            response.put("order", new OrderDTO(order));
            response.put("message", "Cập nhật trạng thái đơn hàng thành công");
            response.put("success", true);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to update order status: " + e.getMessage()));
        }
    }

    @PostMapping("/orders/{id}/payment-status")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updatePaymentStatus(
            @PathVariable Integer id, @RequestParam String paymentStatus, HttpSession session) {
        if (!isAdmin(session)) {
            return ResponseEntity.status(403).body(Map.of("error", "Access denied"));
        }
        try {
            Order order = orderService.updatePaymentStatus(id, paymentStatus);

            // Get updated revenue statistics
            Map<String, Object> revenueStats = orderService.getRevenueStatistics();

            Map<String, Object> response = new HashMap<>();
            response.put("order", new OrderDTO(order));
            response.put("message", "Cập nhật trạng thái thanh toán thành công");
            response.put("revenueUpdated", "PAID".equals(paymentStatus));
            response.put("currentRevenue", revenueStats.get("totalRevenue"));
            response.put("success", true);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to update payment status: " + e.getMessage()));
        }
    }

    @DeleteMapping("/orders/{id}/delete")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteOrder(@PathVariable Integer id, HttpSession session) {
        if (!isAdmin(session)) {
            return ResponseEntity.status(403).body(Map.of("error", "Access denied"));
        }
        try {
            Order order = orderService.findById(id).orElse(null);
            if (order == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Không tìm thấy đơn hàng"));
            }

            // Only allow deletion of cancelled orders
            if (!"CANCELLED".equals(order.getStatus())) {
                return ResponseEntity.badRequest().body(Map.of("error", "Chỉ có thể xóa đơn hàng đã hủy"));
            }

            orderService.deleteOrder(id);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Xóa đơn hàng #" + id + " thành công");
            response.put("success", true);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to delete order: " + e.getMessage()));
        }
    }

    @GetMapping("/orders/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getOrderDetails(@PathVariable Integer id, HttpSession session) {
        if (!isAdmin(session)) {
            return ResponseEntity.status(403).body(Map.of("error", "Access denied"));
        }
        try {
            Order order = orderService.getOrderById(id).orElse(null);
            if (order == null) {
                return ResponseEntity.status(404).body(Map.of("error", "Order not found"));
            }
            Map<String, Object> response = new HashMap<>();
            response.put("order", new OrderDTO(order));
            response.put("success", true);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to load order: " + e.getMessage()));
        }
    }

    // ============ REPORTS ============
    @GetMapping("/reports")
    public String reports(Model model, HttpSession session) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        model.addAttribute("currentPage", "admin-reports");
        return "admin/reports";
    }

    @GetMapping("/reports/data")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getReportsData(HttpSession session) {
        if (!isAdmin(session)) {
            return ResponseEntity.status(403).body(Map.of("error", "Access denied"));
        }
        try {
            // Order statistics
            long totalOrders = orderService.getAllOrders().size();
            long pendingOrders = orderService.getOrderCountByStatus("PENDING");
            long confirmedOrders = orderService.getOrderCountByStatus("CONFIRMED");
            long shippingOrders = orderService.getOrderCountByStatus("SHIPPING");
            long deliveredOrders = orderService.getOrderCountByStatus("DELIVERED");
            long cancelledOrders = orderService.getOrderCountByStatus("CANCELLED");

            // Revenue and payment statistics
            Long totalRevenue = orderService.getTotalRevenue();
            long paidOrdersCount = orderService.getPaidOrdersCount();
            long pendingPaymentCount = orderService.getPendingPaymentOrdersCount();

            // Recent orders
            List<Order> recentOrders = orderService.getRecentOrders();

            // Product statistics
            List<Product> allProducts = productService.getAllProduct();
            long totalProducts = allProducts.size();
            long activeProducts = allProducts.stream()
                    .filter(p -> "ACTIVE".equals(p.getStatus()))
                    .count();

            // Category statistics
            List<Category> allCategories = categoryService.getAllCategory();
            long totalCategories = allCategories.size();

            Map<String, Object> response = new HashMap<>();
            response.put("orderStats", Map.of(
                "total", totalOrders,
                "pending", pendingOrders,
                "confirmed", confirmedOrders,
                "shipping", shippingOrders,
                "delivered", deliveredOrders,
                "cancelled", cancelledOrders
            ));
            response.put("revenueStats", Map.of(
                "total", totalRevenue != null ? totalRevenue : 0,
                "paidOrders", paidOrdersCount,
                "pendingPayment", pendingPaymentCount
            ));
            response.put("productStats", Map.of(
                "total", totalProducts,
                "active", activeProducts
            ));
            response.put("categoryStats", Map.of(
                "total", totalCategories
            ));
            response.put("recentOrders", recentOrders.stream().limit(10).toList());
            response.put("success", true);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to load reports data: " + e.getMessage()));
        }
    }

    @GetMapping("/reports/dashboard-stats")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getDashboardStats(HttpSession session) {
        if (!isAdmin(session)) {
            return ResponseEntity.status(403).body(Map.of("error", "Access denied"));
        }
        try {
            // Order statistics
            long totalOrders = orderService.getOrderCountByStatus("DELIVERED") +
                              orderService.getOrderCountByStatus("PENDING") +
                              orderService.getOrderCountByStatus("CONFIRMED") +
                              orderService.getOrderCountByStatus("SHIPPING");
            long pendingOrders = orderService.getOrderCountByStatus("PENDING");
            long deliveredOrders = orderService.getOrderCountByStatus("DELIVERED");
            long paidOrders = orderService.getPaidOrdersCount();
            long pendingPaymentOrders = orderService.getPendingPaymentOrdersCount();
            Long totalRevenue = orderService.getTotalRevenue();

            // Product statistics
            long totalProducts = productService.getAllProduct().size();
            long activeProducts = productService.getAllProduct().stream()
                .mapToLong(p -> "ACTIVE".equals(p.getStatus()) ? 1 : 0).sum();

            // Category statistics
            long totalCategories = categoryService.getAllCategory().size();

            // User statistics
            long totalUsers = userService.getAllUsers().size();
            long adminUsers = userService.getAllUsers().stream()
                .mapToLong(u -> userService.isAdmin(u) ? 1 : 0).sum();

            // Recent revenue (last 30 days)
            LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
            Long recentRevenue = orderService.getRevenueByDateRange(thirtyDaysAgo, LocalDateTime.now());

            Map<String, Object> stats = new HashMap<>();
            stats.put("orders", Map.of("total", totalOrders, "pending", pendingOrders, "delivered", deliveredOrders));
            stats.put("payments", Map.of("paid", paidOrders, "pending", pendingPaymentOrders));
            stats.put("revenue", Map.of("total", totalRevenue != null ? totalRevenue : 0, "recent", recentRevenue != null ? recentRevenue : 0));
            stats.put("products", Map.of("total", totalProducts, "active", activeProducts));
            stats.put("categories", Map.of("total", totalCategories));
            stats.put("users", Map.of("total", totalUsers, "admin", adminUsers));

            Map<String, Object> response = new HashMap<>();
            response.put("statistics", stats);
            response.put("success", true);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to load statistics: " + e.getMessage()));
        }
    }

    // ============ REDIRECTS & UTILITIES ============
    @GetMapping({"/home", "/shop", "/about", "/contact", "/gallery", "/service", "/services", "/login", "/register", "/cart", "/my-account"})
    public String adminRedirects(HttpSession session) {
        String requestPath = ((jakarta.servlet.http.HttpServletRequest)
            org.springframework.web.context.request.RequestContextHolder.currentRequestAttributes()
                .resolveReference(org.springframework.web.context.request.RequestAttributes.REFERENCE_REQUEST))
                .getRequestURI().replace("/admin", "");

        if (requestPath.equals("/home")) return "redirect:/";
        if (requestPath.equals("/services")) return "redirect:/service";
        return "redirect:" + requestPath;
    }

    @GetMapping({"/product", ""})
    public String adminProduct(HttpSession session, Model model) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        return "redirect:/admin/products";
    }

    @PostMapping("/promote-user")
    @ResponseBody
    public ResponseEntity<String> promoteUser(@RequestParam String username, HttpSession session) {
        if (!isAdmin(session)) {
            return ResponseEntity.badRequest().body("Access denied");
        }
        try {
            userService.promoteToAdmin(username);
            return ResponseEntity.ok("User promoted to admin successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    // ============ HELPER METHODS ============
    // Removed duplicate isAdmin method - now using inherited method from BaseController

    private Map<String, String> validateProductInput(String name, String description, double price, Integer categoryId) {
        Map<String, String> errors = new HashMap<>();
        if (name == null || name.trim().isEmpty()) {
            errors.put("name", "Tên sản phẩm không được để trống");
        }
        if (description == null || description.trim().isEmpty()) {
            errors.put("description", "Mô tả sản phẩm không được để trống");
        }
        if (price <= 0) {
            errors.put("price", "Giá sản phẩm phải lớn hơn 0");
        }
        if (categoryId <= 0) {
            errors.put("categoryId", "Vui lòng chọn danh mục");
        }
        return errors;
    }
}
