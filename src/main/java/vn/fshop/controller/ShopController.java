package vn.fshop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

import vn.fshop.model.Category;
import vn.fshop.model.Product;
import vn.fshop.model.CartItem;
import vn.fshop.model.Image;
import vn.fshop.service.CategoryService;
import vn.fshop.service.ProductService;
import vn.fshop.util.CartUtils;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class ShopController extends BaseController {

	@Autowired
	private CategoryService categoryService;

	@Autowired
	private ProductService productService;

	@GetMapping("/shop")
	public String shop(Model model, @RequestParam(required = false) Integer categoryId, HttpSession session) {
		try {
			model.addAttribute("currentPage", "shop");

			// Add cart information for header
			List<CartItem> cart = getCartFromSession(session);
			model.addAttribute("cart", cart);

			// Get all categories for sidebar
			List<Category> categories = categoryService.getAllCategory();
			model.addAttribute("categories", categories);

			// Get products (visible in shop: ACTIVE and PAUSED)
			List<Product> products;
			if (categoryId != null) {
				products = productService.getProductsByCategoryId(categoryId);
				model.addAttribute("selectedCategoryId", categoryId);
			} else {
				products = productService.getVisibleProducts();
			}

			model.addAttribute("products", products);

			return "shop";
		} catch (Exception e) {
			model.addAttribute("products", new java.util.ArrayList<>());
			model.addAttribute("categories", new java.util.ArrayList<>());
			model.addAttribute("cart", new java.util.ArrayList<>());
			return "shop";
		}
	}

	// AJAX endpoint for filtering products by category
	@GetMapping("/api/shop/products")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getProductsByCategory(
			@RequestParam(required = false) Integer categoryId) {
		try {
			List<Product> products;
			String categoryName = "Tất cả sản phẩm";

			if (categoryId != null) {
				products = productService.getProductsByCategoryId(categoryId);
				Category category = categoryService.getCategoryById(categoryId);
				if (category != null) {
					categoryName = category.getName();
				}
			} else {
				products = productService.getVisibleProducts();
			}

			// Convert products to include images
			List<Map<String, Object>> productList = products.stream().map(product -> {
				Map<String, Object> productMap = new HashMap<>();
				productMap.put("id", product.getId());
				productMap.put("name", product.getName());
				productMap.put("price", product.getPrice());
				productMap.put("discountedPrice", product.getDiscountedPrice());
				productMap.put("description", product.getDescription());
				productMap.put("status", product.getStatus());
				productMap.put("quantity", product.getQuantity());

				// Add category
				if (product.getCategory() != null) {
					Map<String, Object> categoryMap = new HashMap<>();
					categoryMap.put("id", product.getCategory().getId());
					categoryMap.put("name", product.getCategory().getName());
					productMap.put("category", categoryMap);
				}

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

				return productMap;
			}).collect(Collectors.toList());

			Map<String, Object> response = new HashMap<>();
			response.put("products", productList);
			response.put("categoryName", categoryName);
			response.put("productCount", products.size());
			response.put("success", true);

			return ResponseEntity.ok(response);
		} catch (Exception e) {
			Map<String, Object> errorResponse = new HashMap<>();
			errorResponse.put("success", false);
			errorResponse.put("error", "Có lỗi xảy ra khi tải sản phẩm: " + e.getMessage());
			return ResponseEntity.badRequest().body(errorResponse);
		}
	}

	// Search endpoint for traditional form submission
	@GetMapping("/search")
	public String search(Model model, @RequestParam("name-pattern") String searchTerm, HttpSession session) {
		try {
			model.addAttribute("currentPage", "shop");

			// Add cart information for header
			List<CartItem> cart = getCartFromSession(session);
			model.addAttribute("cart", cart);

			// Get all categories for sidebar
			List<Category> categories = categoryService.getAllCategory();
			model.addAttribute("categories", categories);

			// Search products
			List<Product> products;
			if (searchTerm != null && !searchTerm.trim().isEmpty()) {
				products = productService.getProductsByName(searchTerm.trim());
				model.addAttribute("searchTerm", searchTerm.trim());
			} else {
				products = productService.getVisibleProducts();
			}

			model.addAttribute("products", products);
			return "shop";
		} catch (Exception e) {
			model.addAttribute("products", new ArrayList<>());
			model.addAttribute("categories", new ArrayList<>());
			model.addAttribute("cart", new ArrayList<>());
			return "shop";
		}
	}

	// AJAX search endpoint
	@GetMapping("/api/shop/search")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> searchProducts(@RequestParam("q") String searchTerm) {
		try {
			List<Product> products;
			String resultMessage;

			if (searchTerm != null && !searchTerm.trim().isEmpty()) {
				products = productService.getProductsByName(searchTerm.trim());
				resultMessage = "Kết quả tìm kiếm cho: \"" + searchTerm.trim() + "\"";
			} else {
				products = productService.getVisibleProducts();
				resultMessage = "Tất cả sản phẩm";
			}

			// Convert products to include images (reuse existing logic)
			List<Map<String, Object>> productList = products.stream().map(product -> {
				Map<String, Object> productMap = new HashMap<>();
				productMap.put("id", product.getId());
				productMap.put("name", product.getName());
				productMap.put("price", product.getPrice());
				productMap.put("discountedPrice", product.getDiscountedPrice());
				productMap.put("description", product.getDescription());
				productMap.put("status", product.getStatus());
				productMap.put("quantity", product.getQuantity());

				// Add category
				if (product.getCategory() != null) {
					Map<String, Object> categoryMap = new HashMap<>();
					categoryMap.put("id", product.getCategory().getId());
					categoryMap.put("name", product.getCategory().getName());
					productMap.put("category", categoryMap);
				}

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

				return productMap;
			}).collect(Collectors.toList());

			Map<String, Object> response = new HashMap<>();
			response.put("products", productList);
			response.put("searchTerm", searchTerm != null ? searchTerm.trim() : "");
			response.put("resultMessage", resultMessage);
			response.put("productCount", products.size());
			response.put("success", true);

			return ResponseEntity.ok(response);
		} catch (Exception e) {
			Map<String, Object> errorResponse = new HashMap<>();
			errorResponse.put("success", false);
			errorResponse.put("error", "Có lỗi xảy ra khi tìm kiếm: " + e.getMessage());
			return ResponseEntity.badRequest().body(errorResponse);
		}
	}

	// Category redirect functionality (moved from CategoryController)
	@GetMapping("/category")
	public String category(@RequestParam("id") Integer categoryId) {
		return "redirect:/shop?categoryId=" + categoryId;
	}

	// Removed duplicate method - now using inherited method from BaseController
}