package vn.fshop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

import vn.fshop.model.Product;
import vn.fshop.model.CartItem;
import vn.fshop.service.ProductService;

import java.util.List;
import java.util.ArrayList;

@Controller
public class ProductController extends BaseController {

	@Autowired
	private ProductService productService;

	@GetMapping("/api/products/")
	public String getAllProducts(Model model) {
		List<Product> products = productService.getAllProduct();
		model.addAttribute("products", products);
		return "index";
	}

	@GetMapping("/api/products/{id}")
	public String getProductById(@PathVariable Integer id, Model model) {
		Product product = productService.getProductById(id);
		model.addAttribute("product", product);
		return "detail";
	}

	@GetMapping("/product-detail")
	public String productDetail(Model model, @RequestParam("id") Integer productId, HttpSession session) {
		Product product = productService.getProductById(productId);
		if (product == null) {
			return "redirect:/shop";
		}

		model.addAttribute("product", product);
		model.addAttribute("currentPage", "product-detail");

		// Add cart information for header
		List<CartItem> cart = getCartFromSession(session);
		model.addAttribute("cart", cart);

		// Add username for template logic
		String username = (String) session.getAttribute("username");
		model.addAttribute("username", username);

		return "shop-detail";
	}

	// Removed duplicate method - now using inherited method from BaseController
}
