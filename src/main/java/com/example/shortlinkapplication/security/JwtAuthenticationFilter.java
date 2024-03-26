package com.example.shortlinkapplication.security;

import com.example.shortlinkapplication.util.CookieUtils;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenProvider tokenProvider;

    @PostConstruct
    public void init() {
        setAuthenticationManager(authenticationManager);
    }

    public void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws ServletException, IOException {
        // If authentication is successful, generate a JWT token
        String jwt = tokenProvider.createToken(authResult);
        System.out.println("Token: " + jwt);

        // get redirect uri from request parameter
        String redirectUri = request.getParameter("redirect_uri");
        System.out.println("Redirect uri: " + redirectUri);
        if (redirectUri != null) {
            String targetUrl = UriComponentsBuilder.fromUriString(redirectUri)
                    .queryParam("token", jwt)
                    .build().toUriString();
            System.out.println("Target url: " + targetUrl);

            response.sendRedirect(targetUrl);
        }

        // Add token to the Authorization header
        response.addHeader("Authorization", "Bearer " + jwt);

        // Save token in a cookie
        int cookieExpireSeconds = 24 * 60 * 60; // Cookie expires after 24 hours
        CookieUtils.addCookie(response, "token", jwt, cookieExpireSeconds);

        super.successfulAuthentication(request, response, chain, authResult);
    }
}
