package vn.fshop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import vn.fshop.model.CartItem;
import vn.fshop.model.Order;
import vn.fshop.model.Product;
import vn.fshop.model.User;
import vn.fshop.service.OrderService;
import vn.fshop.service.UserService;
import vn.fshop.util.CartUtils;

import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Controller
public class CheckoutController extends BaseController {

    // Redirect any .jsp requests to the correct endpoint
    @RequestMapping("/checkout.jsp")
    public String redirectCheckoutJsp() {
        return "redirect:/checkout";
    }

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @GetMapping("/checkout")
    public String showCheckout(HttpSession session, Model model) {
        // Check if user is logged in
        if (!CartUtils.isUserLoggedIn(session)) {
            return "redirect:/login";
        }

        // Get cart from session
        List<CartItem> cart = getCartFromSession(session);
        if (cart.isEmpty()) {
            return "redirect:/cart";
        }

        // Get user information
        User user = getCurrentUser(session);

        model.addAttribute("cart", cart);
        model.addAttribute("total", CartUtils.calculateCartTotal(cart));
        model.addAttribute("user", user);
        model.addAttribute("currentPage", "checkout");

        return "checkout";
    }

    @PostMapping({"/checkout", "/checkout/", "/checkout.jsp"})
    public String processCheckout(
            @RequestParam String deliveryAddress,
            @RequestParam String phone,
            @RequestParam(required = false) String notes,
            HttpSession session,
            Model model) {



        // Validate delivery address
        if (deliveryAddress == null || deliveryAddress.trim().isEmpty()) {
            model.addAttribute("error", "Vui lòng nhập địa chỉ giao hàng");

            // Re-populate the model for the checkout page
            List<CartItem> cart = getCartFromSession(session);
            double total = CartUtils.calculateCartTotal(cart);

            String username = (String) session.getAttribute("username");
            User user = userService.findByUsername(username).orElse(null);

            model.addAttribute("cart", cart);
            model.addAttribute("total", total);
            model.addAttribute("user", user);
            model.addAttribute("currentPage", "checkout");

            return "checkout";
        }

        try {
            // Check if user is logged in
            if (!CartUtils.isUserLoggedIn(session)) {
                return "redirect:/login";
            }

            // Get cart from session
            List<CartItem> cart = getCartFromSession(session);
            if (cart.isEmpty()) {
                model.addAttribute("error", "Giỏ hàng trống");
                return "redirect:/cart";
            }

            // Get user
            User user = getCurrentUser(session);
            if (user == null) {
                // Clear the invalid session
                session.invalidate();
                model.addAttribute("error", "Phiên đăng nhập không hợp lệ. Vui lòng đăng nhập lại.");
                return "redirect:/login";
            }

            // Create order
            Order order = orderService.createOrderFromCart(user, cart, deliveryAddress, phone, notes);

            // Update user's delivery address for future use
            if (deliveryAddress != null && !deliveryAddress.trim().isEmpty()) {
                user.setDeliveryAddress(deliveryAddress);
                userService.saveUser(user);
            }

            // Clear cart after successful order
            session.setAttribute("cart", new ArrayList<CartItem>());

            // Redirect to order success page
            return "redirect:/order-success?orderId=" + order.getId();

        } catch (Exception e) {
            model.addAttribute("error", "Có lỗi xảy ra khi đặt hàng: " + e.getMessage());

            // Re-populate the model for the checkout page
            List<CartItem> cart = getCartFromSession(session);
            double total = CartUtils.calculateCartTotal(cart);

            String username = (String) session.getAttribute("username");
            User user = userService.findByUsername(username).orElse(null);

            model.addAttribute("cart", cart);
            model.addAttribute("total", total);
            model.addAttribute("user", user);
            model.addAttribute("currentPage", "checkout");

            return "checkout";
        }
    }

    @GetMapping("/order-success")
    public String orderSuccess(@RequestParam Integer orderId, HttpSession session, Model model) {
        // Check if user is logged in
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "redirect:/login";
        }

        // Get order details
        Order order = orderService.getOrderById(orderId).orElse(null);
        if (order == null) {
            return "redirect:/";
        }

        // Verify order belongs to current user
        if (!order.getUser().getUsername().equals(username)) {
            return "redirect:/";
        }

        model.addAttribute("order", order);
        model.addAttribute("currentPage", "order-success");
        
        return "order-success";
    }

    // Removed duplicate method - now using inherited method from BaseController
}
