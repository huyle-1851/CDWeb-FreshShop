package vn.fshop.controller;

import jakarta.servlet.http.HttpServletRequest;
import vn.fshop.model.User;
import vn.fshop.service.ProductService;
import vn.fshop.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller

public class UserController {
	@Autowired
	ProductService productService;
	@Autowired
	UserService userService;

	@GetMapping(value = { "/", "/home","/index" })
	public String homePage(Model model) {
		model.addAttribute("currentPage", "home");
		return "index";
	}

	@GetMapping("/login")
	public String redirectLogin(Model model) {
		model.addAttribute("currentPage", "home");
		model.addAttribute("errorLogin", null);
		return "login";
	}

	@GetMapping("/register")
	public String register(Model model) {
		return "register";
	}

	@GetMapping("/logout")
	public String logout(HttpServletRequest request) {
		request.getSession().removeAttribute("username");
		return "redirect:/";
	}

	@GetMapping("/about")
	public String about(Model model) {
		return "about";
	}

	@GetMapping("/contact")
	public String contact(Model model) {
		return "contact";
	}

	@PostMapping("/login")
	public String login(Model model, HttpServletRequest request, @RequestParam String username,
			@RequestParam String password) {
		User user = userService.login(username, password);
		if (user != null) {
			request.getSession().setAttribute("username", username);
			model.addAttribute("currentPage", "home");
			return "redirect:/";
		} else {
			model.addAttribute("username", username);
			model.addAttribute("errorLogin", true);
			return "redirect:/login";
		}
	}
}
