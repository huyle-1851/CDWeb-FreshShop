package vn.fshop.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import vn.fshop.model.CartItem;
import vn.fshop.model.Product;
import vn.fshop.model.Image;
import vn.fshop.service.ProductService;
import vn.fshop.util.CartUtils;

import java.util.*;
import java.util.stream.Collectors;

@Controller
public class CartController extends BaseController {

    @Autowired
    private ProductService productService;

    @GetMapping("/cart")
    public String viewCart(HttpSession session, Model model) {
        List<CartItem> cart = getCartFromSession(session);
        model.addAttribute("cart", cart);
        model.addAttribute("total", CartUtils.calculateCartTotal(cart));
        model.addAttribute("currentPage", "cart");
        return "cart";
    }

    @PostMapping("/add-item")
    @ResponseBody
    public ResponseEntity<String> addItem(@RequestParam("idP") Integer productId,
                                        @RequestParam("quan") Integer quantity,
                                        @RequestParam("cumulative") boolean cumulative,
                                        HttpSession session) {
        try {
            Product product = productService.getProductById(productId);
            if (product == null) {
                return ResponseEntity.badRequest().body("Product not found");
            }

            List<CartItem> cart = getCartFromSession(session);
            
            // Check if product already exists in cart
            CartItem existingItem = cart.stream()
                    .filter(item -> item.getProduct().getId().equals(productId))
                    .findFirst()
                    .orElse(null);

            if (existingItem != null) {
                if (cumulative) {
                    existingItem.setQuantity(existingItem.getQuantity() + quantity);
                } else {
                    existingItem.setQuantity(quantity);
                }
            } else {
                cart.add(new CartItem(product, quantity));
            }

            session.setAttribute("cart", cart);
            return ResponseEntity.ok("Item added successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error adding item: " + e.getMessage());
        }
    }

    @PostMapping("/remove-item-cart")
    @ResponseBody
    public ResponseEntity<String> removeItem(@RequestParam("idP") Integer productId,
                                           HttpSession session) {
        try {
            List<CartItem> cart = getCartFromSession(session);
            cart.removeIf(item -> item.getProduct().getId().equals(productId));
            session.setAttribute("cart", cart);
            return ResponseEntity.ok("Item removed successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error removing item: " + e.getMessage());
        }
    }

    @PostMapping("/clear-cart")
    @ResponseBody
    public ResponseEntity<String> clearCart(HttpSession session) {
        session.setAttribute("cart", new ArrayList<CartItem>());
        return ResponseEntity.ok("Cart cleared successfully");
    }

    @GetMapping("/cart-count")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getCartCount(HttpSession session) {
        List<CartItem> cart = getCartFromSession(session);
        Map<String, Object> response = new HashMap<>();
        response.put("count", cart.size());
        response.put("success", true);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/cart/items")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getCartItems(HttpSession session) {
        try {
            List<CartItem> cart = getCartFromSession(session);

            // Convert cart items to include images
            List<Map<String, Object>> cartItems = cart.stream().map(item -> {
                Product product = item.getProduct();
                Map<String, Object> itemMap = new HashMap<>();

                // Product info
                Map<String, Object> productMap = new HashMap<>();
                productMap.put("id", product.getId());
                productMap.put("name", product.getName());
                productMap.put("price", product.getPrice());
                productMap.put("discountedPrice", product.getDiscountedPrice());
                productMap.put("description", product.getDescription());

                // Add images
                if (product.getImages() != null && !product.getImages().isEmpty()) {
                    List<Map<String, Object>> imageList = product.getImages().stream().map(image -> {
                        Map<String, Object> imageMap = new HashMap<>();
                        imageMap.put("id", image.getId());
                        imageMap.put("url", image.getUrl());
                        return imageMap;
                    }).collect(Collectors.toList());
                    productMap.put("images", imageList);
                } else {
                    productMap.put("images", new ArrayList<>());
                }

                itemMap.put("product", productMap);
                itemMap.put("quantity", item.getQuantity());

                return itemMap;
            }).collect(Collectors.toList());

            // Calculate total using utility method
            double total = CartUtils.calculateCartTotal(cart);

            Map<String, Object> response = new HashMap<>();
            response.put("items", cartItems);
            response.put("total", total);
            response.put("count", cart.size());
            response.put("success", true);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "Có lỗi xảy ra khi tải giỏ hàng: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    // Removed duplicate method - now using inherited method from BaseController
}
