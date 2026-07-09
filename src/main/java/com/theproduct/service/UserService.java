package com.theproduct.service;

import com.theproduct.model.User;
import com.theproduct.repository.UserRepo;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {

    @Autowired
    AuthenticationManager authenticationManager;
    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private JwtService jwtService;

    public String verify(User user) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                user.getUsername(),
                user.getPassword()
        ));

        if (authentication.isAuthenticated()) {
            return jwtService.generateToken(user.getUsername());
        }
        return "failed";
    }

    // Logout - extract token from Authorization header and invalidate stored token
    public Map<String, Object> logout(HttpServletRequest request) {
        Map<String, Object> resp = new HashMap<>();
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            resp.put("message", "Authorization header missing or invalid");
            return resp;
        }
        String token = authHeader.substring(7).trim();
        String username;
        try {
            username = jwtService.extractUserName(token);
        } catch (Exception ex) {
            resp.put("message", "Invalid token");
            return resp;
        }

        User user1 = userRepo.findByUsername(username);
        if (user1 == null) {
            resp.put("message", "User not found");
            return resp;
        }

        user1.setAuthtoken(null);
        userRepo.save(user1);
        // Clear SecurityContext only if the current authenticated principal matches the user being logged out.
        try {
            org.springframework.security.core.Authentication currentAuth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
            if (currentAuth != null) {
                Object principal = currentAuth.getPrincipal();
                String principalUsername = null;
                if (principal instanceof org.springframework.security.core.userdetails.UserDetails) {
                    principalUsername = ((org.springframework.security.core.userdetails.UserDetails) principal).getUsername();
                } else if (principal instanceof String) {
                    principalUsername = (String) principal;
                }
                if (principalUsername != null && principalUsername.equals(user1.getUsername())) {
                    org.springframework.security.core.context.SecurityContextHolder.clearContext();
                }
            }
        } catch (Exception ex) {
            // ignore any issues clearing context for safety
        }

        resp.put("message", "logged out!!");
        return resp;
    }

    // Login with username + password
    public Map<String, Object> login(User user) {
        Map<String, Object> resp = new HashMap<>();
        User uopt = userRepo.findByUsername(user.getUsername());
        if (uopt == null) {
            resp.put("isExist", false);
            return resp;
        }
        User user1 = uopt;
        if (!encoder.matches(user.getPassword(), user1.getPassword())) {
            resp.put("isExist", true);
            resp.put("error", "Invalid credentials");
            return resp;
        }

        String authToken = jwtService.generateToken(user1.getUsername());
        Map<String, Object> tokens = new HashMap<>();
        tokens.put("authToken", authToken);
        // auth token expiry matches JwtService expiry (30 hours)
        tokens.put("authTokenExpiry", System.currentTimeMillis() + 1000L * 60 * 60 * 30);
        resp.put("isExist", true);
        resp.put("JWT_AUTH", tokens);
        resp.put("user", maskPassword(user1));

        user1.setAuthtoken(authToken);
        userRepo.save(user1);

        return resp;
    }

    // Signup with username + password
    public Map<String, Object> register(User user) {
        Map<String, Object> resp = new HashMap<>();
        User existing = userRepo.findByUsername(user.getUsername());
        if (existing != null) {
            resp.put("isExist", true);
            return resp;
        }

        // create new user
        User newUser = new User();
        newUser.setUsername(user.getUsername());
        newUser.setPassword(encoder.encode(user.getPassword()));

        String authToken = jwtService.generateToken(newUser.getUsername());
        Map<String, Object> tokens = new HashMap<>();
        tokens.put("authToken", authToken);
        // auth token expiry matches JwtService expiry (30 hours)
        tokens.put("authTokenExpiry", System.currentTimeMillis() + 1000L * 60 * 60 * 30);
        resp.put("isExist", true);
        resp.put("JWT_AUTH", tokens);
        resp.put("user", maskPassword(newUser));

        newUser.setAuthtoken(authToken);
        userRepo.save(newUser);

        resp.put("isExist", true);
        resp.put("user", maskPassword(newUser));

        return resp;
    }

    private User maskPassword(User u) {
        User copy = new User();
        copy.setId(u.getId());
        copy.setUsername(u.getUsername());
        return copy;
    }

}
