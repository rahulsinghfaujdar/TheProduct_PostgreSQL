package com.theproduct.controller;

import com.theproduct.model.User;
import com.theproduct.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("")
public class UserController {

    @Autowired
    private UserService userService;


    // Signup endpoint at /register
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody User user) {
        if (user == null || user.getUsername() == null || user.getPassword() == null) {
            Map<String, Object> err = new java.util.HashMap<>();
            err.put("error", "username and password are required");
            return ResponseEntity.badRequest().body(err);
        }
        String email = user.getUsername();
         String password = user.getPassword();
         if (email.trim().isEmpty() || password.trim().isEmpty()) {
            Map<String, Object> err = new java.util.HashMap<>();
            err.put("error", "username and password cannot be empty");
            return ResponseEntity.badRequest().body(err);
        }
        return ResponseEntity.ok(userService.register(user));
    }

    // Also provide /login at root level
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody User user) {
        if (user == null || user.getUsername() == null || user.getPassword() == null) {
            Map<String, Object> err = new java.util.HashMap<>();
            err.put("error", "username and password are required");
            return ResponseEntity.badRequest().body(err);
        }
        String email = user.getUsername();
        String password = user.getPassword();
        if (email.trim().isEmpty() || password.trim().isEmpty()) {
            Map<String, Object> err = new java.util.HashMap<>();
            err.put("error", "username and password cannot be empty");
            return ResponseEntity.badRequest().body(err);
        }
        return ResponseEntity.ok(userService.login(user));
    }

    // Logout
    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(HttpServletRequest request) {
        System.out.println("logout endpoint called");
        return ResponseEntity.ok(userService.logout(request));
    }
}
