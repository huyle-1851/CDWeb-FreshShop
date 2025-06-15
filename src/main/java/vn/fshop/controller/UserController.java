package vn.fshop.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import vn.fshop.model.User;
import vn.fshop.model.CartItem;
import vn.fshop.service.UserService;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller

public class UserController extends BaseController {

	@GetMapping(value = { "/", "/home","/index" })
	public String homePage(Model model, HttpServletRequest request) {
		model.addAttribute("currentPage", "home");

		// Add cart information for header
		List<CartItem> cart = getCartFromSession(request.getSession());
		model.addAttribute("cart", cart);

		// Check if user is logged in and get user info
		String username = (String) request.getSession().getAttribute("username");
		if (username != null) {
			User user = userService.findByUsername(username).orElse(null);
			if (user != null) {
				model.addAttribute("currentUser", user);
				model.addAttribute("isAdmin", userService.isAdmin(user));
			}
		}

		return "index";
	}

	@GetMapping("/login")
	public String redirectLogin(Model model, HttpServletRequest request) {
		model.addAttribute("currentPage", "home");
		model.addAttribute("errorLogin", null);

		// Add cart information for header
		List<CartItem> cart = getCartFromSession(request.getSession());
		model.addAttribute("cart", cart);

		return "login";
	}

	 @GetMapping("/register")
	    public String showRegisterForm(Model model, HttpServletRequest request) {
	        model.addAttribute("errors", new HashMap<String, String>());

	        // Add cart information for header
	        List<CartItem> cart = getCartFromSession(request.getSession());
	        model.addAttribute("cart", cart);

	        return "register";
	    }

	    @PostMapping("/register")
	    public String handleRegister(@RequestParam Map<String, String> params, Model model, HttpServletRequest request) {
	        Map<String, String> errors = new HashMap<>();
	        String lastname = params.get("lastname");
	        String firstname = params.get("firstname");
	        String phone = params.get("phone");
	        String address = params.get("address");
	        String username = params.get("usernameIn");
	        String email = params.get("email");
	        String password = params.get("password");
	        String pwconfirm = params.get("pwconfirm");

	        // Validation
	        if (lastname == null || lastname.trim().isEmpty()) {
	            errors.put("lastname", "Vui lòng nhập họ và tên đệm.");
	        }

	        if (firstname == null || firstname.trim().isEmpty()) {
	            errors.put("firstname", "Vui lòng nhập tên.");
	        }

	        if (phone == null || !phone.matches("\\d{10}")) {
	            errors.put("phone", "Số điện thoại không hợp lệ (10 chữ số).");
	        }

	        if (address == null || address.trim().isEmpty()) {
	            errors.put("address", "Vui lòng nhập địa chỉ.");
	        }

	        if (username == null || username.trim().isEmpty()) {
	            errors.put("username", "Vui lòng nhập tên tài khoản.");
	        } else if (userService.existsByUsername(username)) {
	            errors.put("username", "Tên tài khoản đã tồn tại.");
	        }

	        if (email == null || !email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$")) {
	            errors.put("email", "Email không hợp lệ.");
	        } else if (userService.existsByEmail(email)) {
	            errors.put("email", "Email đã được sử dụng.");
	        }

	        if (password == null || password.length() < 6) {
	            errors.put("password", "Mật khẩu phải có ít nhất 6 ký tự.");
	        }

	        if (pwconfirm == null || !pwconfirm.equals(password)) {
	            errors.put("pwconfirm", "Mật khẩu xác nhận không khớp.");
	        }

	        if (!errors.isEmpty()) {
	            model.addAttribute("errors", errors);
	            model.addAttribute("formData", params); // Preserve form data

	            // Add cart information for header
	            List<CartItem> cart = getCartFromSession(request.getSession());
	            model.addAttribute("cart", cart);

	            return "register";
	        }

	        try {
	            // Register the user
	            userService.register(username, password, firstname, lastname, phone, email, address);
	            model.addAttribute("successMessage", "Đăng ký thành công! Vui lòng đăng nhập.");
	            return "redirect:/login?success=true";
	        } catch (Exception e) {
	            errors.put("general", "Có lỗi xảy ra: " + e.getMessage());
	            model.addAttribute("errors", errors);
	            model.addAttribute("formData", params);

	            // Add cart information for header
	            List<CartItem> cart = getCartFromSession(request.getSession());
	            model.addAttribute("cart", cart);

	            return "register";
	        }
	    }

	    // AJAX Register endpoint
	    @PostMapping("/api/register")
	    @ResponseBody
	    public ResponseEntity<Map<String, Object>> registerAjax(@RequestParam Map<String, String> params) {
	        try {
	            Map<String, String> errors = new HashMap<>();
	            String lastname = params.get("lastname");
	            String firstname = params.get("firstname");
	            String phone = params.get("phone");
	            String address = params.get("address");
	            String username = params.get("usernameIn");
	            String email = params.get("email");
	            String password = params.get("password");
	            String pwconfirm = params.get("pwconfirm");

	            // Validation
	            if (lastname == null || lastname.trim().isEmpty()) {
	                errors.put("lastname", "Vui lòng nhập họ và tên đệm");
	            }

	            if (firstname == null || firstname.trim().isEmpty()) {
	                errors.put("firstname", "Vui lòng nhập tên");
	            }

	            if (phone == null || !phone.matches("\\d{10}")) {
	                errors.put("phone", "Số điện thoại không hợp lệ (10 chữ số)");
	            }

	            if (address == null || address.trim().isEmpty()) {
	                errors.put("address", "Vui lòng nhập địa chỉ");
	            }

	            if (username == null || username.trim().isEmpty()) {
	                errors.put("username", "Vui lòng nhập tên tài khoản");
	            } else if (userService.existsByUsername(username.trim())) {
	                errors.put("username", "Tên tài khoản đã tồn tại");
	            }

	            if (email == null || !email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$")) {
	                errors.put("email", "Email không hợp lệ");
	            } else if (userService.existsByEmail(email.trim())) {
	                errors.put("email", "Email đã được sử dụng");
	            }

	            if (password == null || password.length() < 6) {
	                errors.put("password", "Mật khẩu phải có ít nhất 6 ký tự");
	            }

	            if (pwconfirm == null || !pwconfirm.equals(password)) {
	                errors.put("pwconfirm", "Mật khẩu xác nhận không khớp");
	            }

	            if (!errors.isEmpty()) {
	                return ResponseEntity.badRequest().body(Map.of("errors", errors, "success", false));
	            }

	            // Register the user
	            User newUser = userService.register(username.trim(), password, firstname.trim(),
	                lastname.trim(), phone.trim(), email.trim(), address.trim());

	            Map<String, Object> response = new HashMap<>();
	            response.put("success", true);
	            response.put("message", "Đăng ký thành công! Bạn có thể đăng nhập ngay bây giờ.");
	            response.put("redirectUrl", "/login");

	            return ResponseEntity.ok(response);
	        } catch (Exception e) {
	            System.err.println("AJAX Register error: " + e.getMessage());
	            e.printStackTrace();
	            return ResponseEntity.badRequest().body(Map.of(
	                "success", false,
	                "error", "Có lỗi xảy ra: " + e.getMessage()
	            ));
	        }
	    }

	    // AJAX endpoint to check username availability
	    @PostMapping("/api/check-username")
	    @ResponseBody
	    public ResponseEntity<Map<String, Object>> checkUsernameAvailability(@RequestParam String username) {
	        try {
	            boolean available = !userService.existsByUsername(username.trim());
	            return ResponseEntity.ok(Map.of("available", available));
	        } catch (Exception e) {
	            return ResponseEntity.ok(Map.of("available", true)); // Default to available on error
	        }
	    }

	    // AJAX endpoint to check email availability
	    @PostMapping("/api/check-email")
	    @ResponseBody
	    public ResponseEntity<Map<String, Object>> checkEmailAvailability(@RequestParam String email) {
	        try {
	            boolean available = !userService.existsByEmail(email.trim());
	            return ResponseEntity.ok(Map.of("available", available));
	        } catch (Exception e) {
	            return ResponseEntity.ok(Map.of("available", true)); // Default to available on error
	        }
	    }

	@GetMapping("/about")
	public String about(Model model, HttpServletRequest request) {
		// Add cart information for header
		List<CartItem> cart = getCartFromSession(request.getSession());
		model.addAttribute("cart", cart);
		return "about";
	}

	@GetMapping("/contact")
	public String contact(Model model, HttpServletRequest request) {
		// Add cart information for header
		List<CartItem> cart = getCartFromSession(request.getSession());
		model.addAttribute("cart", cart);
		return "contact";
	}

	@GetMapping("/my-account")
	public String myAccount(Model model, HttpServletRequest request) {
		model.addAttribute("currentPage", "my-account");

		// Add cart information for header
		List<CartItem> cart = getCartFromSession(request.getSession());
		model.addAttribute("cart", cart);

		// Check if user is logged in
		String username = (String) request.getSession().getAttribute("username");
		if (username == null) {
			return "redirect:/login";
		}

		// Get user information
		User user = userService.findByUsername(username).orElse(null);
		if (user != null) {
			model.addAttribute("currentUser", user);
			model.addAttribute("isAdmin", userService.isAdmin(user));
		}

		return "my-account";
	}



	@PostMapping("/login")
	public String login(Model model, HttpServletRequest request, @RequestParam String username,
			@RequestParam String password) {
		User user = userService.login(username, password);

		if (user != null) {
			request.getSession().setAttribute("username", username);
			request.getSession().setAttribute("isAdmin", userService.isAdmin(user));
			model.addAttribute("currentPage", "home");
			return "redirect:/";
		} else {
			model.addAttribute("username", username);
			model.addAttribute("errorLogin", true);
			return "redirect:/login";
		}
	}

	// AJAX Login endpoint
	@PostMapping("/api/login")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> loginAjax(@RequestParam String username,
			@RequestParam String password, HttpServletRequest request) {
		try {
			// Validate input
			Map<String, String> errors = new HashMap<>();

			if (username == null || username.trim().isEmpty()) {
				errors.put("username", "Vui lòng nhập tên đăng nhập");
			}
			if (password == null || password.trim().isEmpty()) {
				errors.put("password", "Vui lòng nhập mật khẩu");
			}

			if (!errors.isEmpty()) {
				return ResponseEntity.badRequest().body(Map.of("errors", errors, "success", false));
			}

			User user = userService.login(username.trim(), password);

			if (user != null) {
				request.getSession().setAttribute("username", username.trim());
				request.getSession().setAttribute("isAdmin", userService.isAdmin(user));

				Map<String, Object> response = new HashMap<>();
				response.put("success", true);
				response.put("message", "Đăng nhập thành công!");
				response.put("redirectUrl", "/");
				response.put("user", Map.of(
					"username", user.getUsername(),
					"firstname", user.getFirstname(),
					"lastname", user.getLastname(),
					"isAdmin", userService.isAdmin(user)
				));

				return ResponseEntity.ok(response);
			} else {
				return ResponseEntity.badRequest().body(Map.of(
					"success", false,
					"error", "Tên đăng nhập hoặc mật khẩu không đúng"
				));
			}
		} catch (Exception e) {
			System.err.println("AJAX Login error: " + e.getMessage());
			e.printStackTrace();
			return ResponseEntity.badRequest().body(Map.of(
				"success", false,
				"error", "Có lỗi xảy ra: " + e.getMessage()
			));
		}
	}



	// Removed duplicate method - now using inherited method from BaseController
}
