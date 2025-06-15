package vn.fshop.exception;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.logging.Logger;

@ControllerAdvice
@Controller
public class GlobalExceptionHandler implements ErrorController {

    private static final Logger logger = Logger.getLogger(GlobalExceptionHandler.class.getName());

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ModelAndView handleGenericException(HttpServletRequest request, Exception ex) {
        String message = ex.getMessage();

        // Ignore harmless static resource errors for .map files
        if (message != null && (message.contains(".js.map") || message.contains(".css.map") ||
            message.contains("No static resource"))) {
            // Don't log these harmless static resource errors
            ModelAndView mav = new ModelAndView();
            mav.setViewName("error");
            return mav;
        }

        logger.severe("Unexpected error occurred: " + message);

        ModelAndView mav = new ModelAndView();
        mav.addObject("exception", ex);
        mav.addObject("url", request.getRequestURL());
        mav.addObject("message", "An unexpected error occurred. Please try again later.");
        mav.setViewName("error");
        return mav;
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ModelAndView handleRuntimeException(HttpServletRequest request, RuntimeException ex) {
        logger.warning("Runtime error occurred: " + ex.getMessage());
        
        ModelAndView mav = new ModelAndView();
        mav.addObject("exception", ex);
        mav.addObject("url", request.getRequestURL());
        mav.addObject("message", ex.getMessage());
        mav.setViewName("error");
        return mav;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ModelAndView handleIllegalArgumentException(HttpServletRequest request, IllegalArgumentException ex) {
        logger.warning("Invalid argument: " + ex.getMessage());
        
        ModelAndView mav = new ModelAndView();
        mav.addObject("exception", ex);
        mav.addObject("url", request.getRequestURL());
        mav.addObject("message", "Invalid request parameters: " + ex.getMessage());
        mav.setViewName("error");
        return mav;
    }

    // Handle 404 errors and admin route redirects
    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, HttpServletResponse response) {
        String requestURI = request.getRequestURI();
        Integer statusCode = (Integer) request.getAttribute("jakarta.servlet.error.status_code");

        // Handle admin route errors
        if (requestURI != null && requestURI.startsWith("/admin/")) {
            // Check if it's a common page that should redirect to normal version
            if (requestURI.contains("/shop")) {
                return "redirect:/shop";
            } else if (requestURI.contains("/about")) {
                return "redirect:/about";
            } else if (requestURI.contains("/contact")) {
                return "redirect:/contact";
            } else if (requestURI.contains("/gallery")) {
                return "redirect:/gallery";
            } else if (requestURI.contains("/service")) {
                return "redirect:/service";
            } else if (requestURI.contains("/login")) {
                return "redirect:/login";
            } else if (requestURI.contains("/register")) {
                return "redirect:/register";
            } else if (requestURI.contains("/cart")) {
                return "redirect:/cart";
            } else if (requestURI.contains("/checkout")) {
                return "redirect:/checkout";
            } else if (requestURI.contains("/my-account") || requestURI.contains("/account")) {
                return "redirect:/my-account";
            }
            // Admin management routes
            else if (requestURI.contains("/product")) {
                return "redirect:/admin/products";
            } else if (requestURI.contains("/order")) {
                return "redirect:/admin/orders";
            } else if (requestURI.contains("/category") || requestURI.contains("/categories")) {
                return "redirect:/admin/categories";
            } else if (requestURI.contains("/report")) {
                return "redirect:/admin/reports";
            } else {
                // Default admin redirect to dashboard
                return "redirect:/admin/dashboard";
            }
        }

        // Handle other 404 errors
        if (statusCode != null && statusCode == 404) {
            return "redirect:/";
        }

        // Default error handling
        return "error";
    }
}
