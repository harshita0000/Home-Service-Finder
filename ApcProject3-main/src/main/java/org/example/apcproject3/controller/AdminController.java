package org.example.apcproject3.controller;

import org.example.apcproject3.entity.User;
import org.example.apcproject3.entity.UserRole;
import org.example.apcproject3.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private UserService userService;

    @GetMapping("/dashboard")
    public String adminDashboard(Model model) {
        // Get user statistics
        long totalUsers = userService.getTotalUsersCount();
        long activeUsers = userService.getActiveUsersCount();
        long customerCount = userService.getUsersByRoleCount(UserRole.CUSTOMER);
        long providerCount = userService.getUsersByRoleCount(UserRole.SERVICE_PROVIDER);
        long adminCount = userService.getUsersByRoleCount(UserRole.ADMIN);

        // Get recent users
        List<User> recentUsers = userService.getRecentlyRegisteredUsers(5);

        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("activeUsers", activeUsers);
        model.addAttribute("customerCount", customerCount);
        model.addAttribute("providerCount", providerCount);
        model.addAttribute("adminCount", adminCount);
        model.addAttribute("recentUsers", recentUsers);

        return "admin/dashboard";
    }

    @GetMapping("/users")
    public String manageUsers(Model model,
                             @RequestParam(value = "role", required = false) String role,
                             @RequestParam(value = "status", required = false) String status) {
        List<User> users;

        if (role != null && !role.isEmpty()) {
            try {
                UserRole userRole = UserRole.valueOf(role.toUpperCase());
                users = userService.findUsersByRole(userRole);
            } catch (IllegalArgumentException e) {
                users = userService.findAllUsers();
            }
        } else if (status != null && !status.isEmpty()) {
            boolean enabled = "active".equalsIgnoreCase(status);
            users = userService.findActiveUsers();
        } else {
            users = userService.findAllUsers();
        }

        model.addAttribute("users", users);
        model.addAttribute("selectedRole", role);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("userRoles", UserRole.values());

        return "admin/users";
    }

    @GetMapping("/users/{id}")
    public String viewUser(@PathVariable Long id, Model model) {
        User user = userService.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        model.addAttribute("user", user);
        model.addAttribute("userRoles", UserRole.values());

        return "admin/user-detail";
    }

    @PostMapping("/users/{id}/toggle-status")
    public String toggleUserStatus(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            User user = userService.toggleUserStatus(id);
            String status = user.isEnabled() ? "enabled" : "disabled";
            redirectAttributes.addFlashAttribute("success",
                "User " + user.getUsername() + " has been " + status + " successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                "Failed to update user status: " + e.getMessage());
        }

        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/update-role")
    public String updateUserRole(@PathVariable Long id,
                               @RequestParam("role") String role,
                               RedirectAttributes redirectAttributes) {
        try {
            UserRole newRole = UserRole.valueOf(role.toUpperCase());
            User user = userService.updateUserRole(id, newRole);
            redirectAttributes.addFlashAttribute("success",
                "User " + user.getUsername() + " role updated to " + newRole.name() + " successfully.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", "Invalid role selected.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                "Failed to update user role: " + e.getMessage());
        }

        return "redirect:/admin/users/" + id;
    }

    @PostMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            User user = userService.findById(id)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Prevent admin from deleting themselves
            // This would need to be enhanced to check current user
            userService.deleteUser(id);
            redirectAttributes.addFlashAttribute("success",
                "User " + user.getUsername() + " has been deleted successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                "Failed to delete user: " + e.getMessage());
        }

        return "redirect:/admin/users";
    }

    @GetMapping("/create-user")
    public String createUserForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("userRoles", UserRole.values());
        return "admin/create-user";
    }

    @PostMapping("/create-user")
    public String createUser(@ModelAttribute User user,
                           @RequestParam("role") String role,
                           RedirectAttributes redirectAttributes) {
        try {
            UserRole userRole = UserRole.valueOf(role.toUpperCase());
            user.setRole(userRole);
            user.setEnabled(true);

            User createdUser = userService.createUser(user);
            redirectAttributes.addFlashAttribute("success",
                "User " + createdUser.getUsername() + " created successfully.");

            return "redirect:/admin/users";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                "Failed to create user: " + e.getMessage());
            return "redirect:/admin/create-user";
        }
    }

    @GetMapping("/system-info")
    public String systemInfo(Model model) {
        // Add system information if needed
        model.addAttribute("javaVersion", System.getProperty("java.version"));
        model.addAttribute("springBootVersion", "3.x"); // You can get this dynamically

        return "admin/system-info";
    }
}
