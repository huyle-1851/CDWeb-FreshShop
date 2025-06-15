package vn.fshop.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import vn.fshop.model.CartItem;
import vn.fshop.model.User;
import vn.fshop.service.UserService;
import vn.fshop.util.CartUtils;

import java.util.List;

/**
 * Base controller with common functionality to reduce code duplication
 */
public abstract class BaseController {
    
    @Autowired
    protected UserService userService;
    
    /**
     * Get cart from session
     */
    protected List<CartItem> getCartFromSession(HttpSession session) {
        return CartUtils.getCartFromSession(session);
    }
    
    /**
     * Calculate cart total
     */
    protected double calculateCartTotal(List<CartItem> cart) {
        return CartUtils.calculateCartTotal(cart);
    }
    
    /**
     * Add common model attributes for pages
     */
    protected void addCommonAttributes(Model model, HttpSession session, String currentPage) {
        // Add cart information
        List<CartItem> cart = getCartFromSession(session);
        model.addAttribute("cart", cart);
        model.addAttribute("currentPage", currentPage);
        
        // Add user information if logged in
        String username = CartUtils.getUsernameFromSession(session);
        if (username != null) {
            User user = userService.findByUsername(username).orElse(null);
            if (user != null) {
                model.addAttribute("currentUser", user);
                model.addAttribute("isAdmin", userService.isAdmin(user));
            }
        }
    }
    
    /**
     * Check if current user is admin
     */
    protected boolean isAdmin(HttpSession session) {
        String username = CartUtils.getUsernameFromSession(session);
        if (username == null) return false;
        
        User user = userService.findByUsername(username).orElse(null);
        return user != null && userService.isAdmin(user);
    }
    
    /**
     * Get current user from session
     */
    protected User getCurrentUser(HttpSession session) {
        String username = CartUtils.getUsernameFromSession(session);
        if (username == null) return null;
        
        return userService.findByUsername(username).orElse(null);
    }
}
