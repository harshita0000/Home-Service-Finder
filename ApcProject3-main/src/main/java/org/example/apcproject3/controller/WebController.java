package org.example.apcproject3.controller;

import org.example.apcproject3.entity.ServiceCategory;
import org.example.apcproject3.entity.ServiceProvider;
import org.example.apcproject3.entity.User;
import org.example.apcproject3.entity.UserRole;
import org.example.apcproject3.service.ServiceCategoryService;
import org.example.apcproject3.service.ServiceProviderService;
import org.example.apcproject3.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Controller
public class WebController {

    @Autowired
    private ServiceCategoryService serviceCategoryService;

    @Autowired
    private ServiceProviderService serviceProviderService;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @GetMapping("/")
    public String home(Model model) {
        List<ServiceCategory> categories = serviceCategoryService.findActiveCategoriesOrdered();
        List<ServiceProvider> topProviders = serviceProviderService.findTopRatedProviders(new BigDecimal("4.0"));

        model.addAttribute("categories", categories);
        model.addAttribute("topProviders", topProviders.subList(0, Math.min(6, topProviders.size())));

        return "index";
    }

    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                           @RequestParam(value = "logout", required = false) String logout,
                           Model model) {
        if (error != null) {
            model.addAttribute("error", "Invalid username/email or password!");
        }
        if (logout != null) {
            model.addAttribute("message", "You've been logged out successfully.");
        }
        return "auth/login";
    }

    @GetMapping("/simple-login")
    public String simpleLoginPage() {
        return "auth/simple-login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("user", new User());
        return "auth/register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") User user,
                              BindingResult result,
                              @RequestParam("confirmPassword") String confirmPassword,
                              @RequestParam(value = "role", defaultValue = "CUSTOMER") String role,
                              Model model,
                              RedirectAttributes redirectAttributes,
                              HttpServletRequest request) {

        if (result.hasErrors()) {
            return "auth/register";
        }

        if (!user.getPassword().equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match!");
            return "auth/register";
        }

        try {
            // Check if username or email already exists
            if (userService.existsByUsername(user.getUsername())) {
                model.addAttribute("error", "Username is already taken!");
                return "auth/register";
            }

            if (userService.existsByEmail(user.getEmail())) {
                model.addAttribute("error", "Email is already in use!");
                return "auth/register";
            }

            // Set user properties (let UserService handle password encoding)
            try {
                user.setRole(UserRole.valueOf(role.toUpperCase()));
            } catch (IllegalArgumentException e) {
                user.setRole(UserRole.CUSTOMER);
            }
            user.setEnabled(true);

            User createdUser = userService.createUser(user);

            // Auto-login the user after successful registration
            try {
                // Use the original password for authentication (before encoding)
                UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(createdUser.getUsername(), confirmPassword);
                Authentication authentication = authenticationManager.authenticate(authToken);
                SecurityContextHolder.getContext().setAuthentication(authentication);

                // Create session
                HttpSession session = request.getSession(true);
                session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

                redirectAttributes.addFlashAttribute("message", "Registration successful! Welcome to Urban Services!");
                return "redirect:/dashboard";

            } catch (Exception authException) {
                System.err.println("Auto-login failed after registration: " + authException.getMessage());
                // If auto-login fails, redirect to login page with success message
                redirectAttributes.addFlashAttribute("message", "Registration successful! Please login.");
                return "redirect:/login";
            }

        } catch (Exception e) {
            System.err.println("Registration error: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Registration failed: " + e.getMessage());
            return "auth/register";
        }
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            User user = (User) authentication.getPrincipal();
            model.addAttribute("user", user);

            // Redirect based on user role
            switch (user.getRole()) {
                case ADMIN:
                    return "dashboard/admin-dashboard";
                case SERVICE_PROVIDER:
                    return "dashboard/provider-dashboard";
                case CUSTOMER:
                default:
                    return "dashboard/customer-dashboard";
            }
        }
        return "redirect:/login";
    }

    @GetMapping("/profile")
    public String profile(Model model, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            User user = (User) authentication.getPrincipal();
            model.addAttribute("user", user);
            return "profile/profile";
        }
        return "redirect:/login";
    }

    @GetMapping("/services")
    public String services(Model model) {
        List<ServiceCategory> categories = serviceCategoryService.findActiveCategoriesOrdered();
        model.addAttribute("categories", categories);
        return "services/categories";
    }

    @GetMapping("/services/category/{id}")
    public String serviceProviders(@PathVariable Long id, Model model) {
        Optional<ServiceCategory> category = serviceCategoryService.findById(id);
        if (category.isPresent()) {
            List<ServiceProvider> providers = serviceProviderService.findAvailableProvidersByCategory(category.get());
            model.addAttribute("category", category.get());
            model.addAttribute("providers", providers);
        }
        return "services/providers";
    }

    @GetMapping("/provider/{id}")
    public String providerProfile(@PathVariable Long id, Model model) {
        Optional<ServiceProvider> provider = serviceProviderService.findById(id);
        if (provider.isPresent()) {
            model.addAttribute("provider", provider.get());
        }
        return "services/provider-profile";
    }

    @GetMapping("/booking")
    public String bookingPage(@RequestParam(required = false) Long providerId, Model model) {
        if (providerId != null) {
            Optional<ServiceProvider> provider = serviceProviderService.findById(providerId);
            if (provider.isPresent()) {
                model.addAttribute("provider", provider.get());
            }
        }
        return "booking/create";
    }

    @GetMapping("/my-bookings")
    public String myBookings() {
        return "booking/my-bookings";
    }

    @GetMapping("/provider-dashboard")
    public String providerDashboard() {
        return "provider/dashboard";
    }

    @GetMapping("/admin")
    public String adminDashboard() {
        return "admin/dashboard";
    }

    @GetMapping("/analytics")
    public String analyticsPage(Model model, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            User user = (User) authentication.getPrincipal();
            if (user.getRole() == UserRole.ADMIN) {
                model.addAttribute("user", user);
                return "analytics/dashboard";
            }
        }
        return "redirect:/login";
    }
}
