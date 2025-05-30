package vn.fshop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import vn.fshop.model.Category;
import vn.fshop.model.Product;
import vn.fshop.service.CategoryService;
import vn.fshop.service.ProductService;
import java.util.*;

@Controller
public class ShopController {
	@Autowired
	CategoryService categoryService;
	@Autowired
	ProductService productService;

	@GetMapping("/shop")
	public String shop(Model model) {
		model.addAttribute("currentPage", "shop");
		List<Category> categories = categoryService.getAllCategory();
		model.addAttribute("categories", categories);
		ArrayList<Product> productsOfCategory = (ArrayList<Product>) model.getAttribute("productsOfCategory");
		Object category_id = model.getAttribute("category_id");

		if (category_id != null)
			model.addAttribute("category_id", category_id);

		ArrayList<Product> currProductList = (ArrayList<Product>) model.getAttribute("products");
		if (currProductList != null)
			model.addAttribute("products", currProductList);
		else {
			if (productsOfCategory == null)
				currProductList = (ArrayList<Product>) productService.getAllProduct();
			else
				currProductList = productsOfCategory;
			model.addAttribute("products", currProductList);
			ArrayList<Product> productsSearched = (ArrayList<Product>) model.getAttribute("products-searched");
			if (productsSearched != null)
				model.addAttribute("products", productsSearched);
		}
		return "shop";
	}
}