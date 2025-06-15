package vn.fshop.util;

import jakarta.servlet.http.HttpSession;
import vn.fshop.model.CartItem;
import vn.fshop.model.Product;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for cart operations to eliminate duplicate code across controllers
 */
public class CartUtils {
    
    /**
     * Get cart from session, create new if doesn't exist
     */
    @SuppressWarnings("unchecked")
    public static List<CartItem> getCartFromSession(HttpSession session) {
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        if (cart == null) {
            cart = new ArrayList<>();
            session.setAttribute("cart", cart);
        }
        return cart;
    }
    
    /**
     * Calculate effective price for a product (discounted or regular)
     */
    public static double getEffectivePrice(Product product) {
        return product.getDiscountedPrice() > 0 && 
               product.getDiscountedPrice() != product.getPrice() 
               ? product.getDiscountedPrice() 
               : product.getPrice();
    }
    
    /**
     * Calculate total for cart items
     */
    public static double calculateCartTotal(List<CartItem> cart) {
        return cart.stream()
                .mapToDouble(item -> getEffectivePrice(item.getProduct()) * item.getQuantity())
                .sum();
    }
    
    /**
     * Calculate total as integer for cart items
     */
    public static int calculateCartTotalAsInt(List<CartItem> cart) {
        return cart.stream()
                .mapToInt(item -> (int)(getEffectivePrice(item.getProduct()) * item.getQuantity()))
                .sum();
    }
    
    /**
     * Check if user is logged in
     */
    public static boolean isUserLoggedIn(HttpSession session) {
        return session.getAttribute("username") != null;
    }
    
    /**
     * Get username from session
     */
    public static String getUsernameFromSession(HttpSession session) {
        return (String) session.getAttribute("username");
    }
}
