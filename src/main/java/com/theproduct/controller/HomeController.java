package com.theproduct.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@RestController
public class HomeController {

    @GetMapping({"", "/"})
    public String greet(HttpServletRequest request) {

        System.out.println(request.toString());

        Collections.list(request.getHeaderNames())
                .forEach(
                        (str) ->
                        {
                            System.out.println(str + " : " + request.getHeader(str));
                        }
                );

        // CSRF Token (safe check)
        CsrfToken csrf = (CsrfToken) request.getAttribute("_csrf");
        String csrfToken = (csrf != null) ? csrf.getToken() : "Not available";

        String reqDetails = "Method: " + request.getMethod() + "\n" +
                "URL: " + request.getRequestURL() + "\n" +
                "URI: " + request.getRequestURI() + "\n" +
                "Query: " + request.getQueryString() + "\n" +
                "Client IP: " + request.getRemoteAddr() + "\n" +
                "Session ID: " + request.getSession().getId() + "\n" +
                "CSRF Token : " + csrfToken + "\n";

        return "Welcome !!\n\n" + reqDetails;
    }

}
