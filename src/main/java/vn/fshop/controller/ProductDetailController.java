package vn.fshop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import vn.fshop.model.Product;
import vn.fshop.service.ProductService;

@Controller
public class ProductDetailController {
    @Autowired
    ProductService productService;
    
    @GetMapping("/product-detail")
    protected String productDetail(Model model, @RequestParam("id") String productId) {
        int id = Integer.parseInt(productId);
        Product product = productService.getProductById(id);
        model.addAttribute("product", product);
        return "shop-detail";
    }
}
