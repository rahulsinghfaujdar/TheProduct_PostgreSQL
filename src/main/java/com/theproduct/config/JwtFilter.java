package com.theproduct.config;

import com.theproduct.service.JwtService;
import com.theproduct.service.UserDetailService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.beans.factory.annotation.Autowired;
import com.theproduct.repository.UserRepo;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    ApplicationContext context;

    @Autowired
    private UserRepo userRepo;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        System.out.println("authHeader : "+authHeader);
        String token = null;
        String username = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7).trim();
            try {
                username = jwtService.extractUserName(token);
            } catch (io.jsonwebtoken.JwtException ex) {
                // invalid token - respond with 401 Unauthorized
                System.out.println("Invalid JWT token: " + ex.getMessage());
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired JWT token");
                return;
            } catch (Exception ex) {
                System.out.println("Error parsing JWT token: " + ex.getMessage());
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT token");
                return;
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = context.getBean(UserDetailService.class).loadUserByUsername(username);
            try {
                if (jwtService.validateToken(token, userDetails)) {
                    // additionally check that the token matches the one stored for the user (invalidate on logout)
                    com.theproduct.model.User u = userRepo.findByUsername(userDetails.getUsername());
                    if (u == null) {
                        u = userRepo.findByUsername(userDetails.getUsername());
                    }
                    if (u != null && u.getAuthtoken() != null && u.getAuthtoken().equals(token)) {
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        authToken.setDetails(new WebAuthenticationDetailsSource()
                                .buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    } else {
                        System.out.println("JWT token does not match stored user token or user not found");
                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token or logged out");
                        return;
                    }
                }
            } catch (io.jsonwebtoken.JwtException ex) {
                System.out.println("JWT validation failed: " + ex.getMessage());
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT validation failed");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

}
