package com.example.shortlinkapplication.controller;

import com.example.shortlinkapplication.dto.*;
import com.example.shortlinkapplication.entity.AuthProvider;
import com.example.shortlinkapplication.entity.User;
import com.example.shortlinkapplication.repository.UserRepository;
import com.example.shortlinkapplication.security.JwtAuthenticationFilter;
import com.example.shortlinkapplication.security.TokenProvider;
import com.example.shortlinkapplication.security.oauth.OAuth2AuthenticationFailureHandler;
import com.example.shortlinkapplication.security.oauth.OAuth2AuthenticationSuccessHandler;
import com.example.shortlinkapplication.service.LoginService;
import com.example.shortlinkapplication.service.UserService;
import com.example.shortlinkapplication.util.CookieUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.servlet.view.RedirectView;


import java.io.IOException;
import java.net.URI;

@RestController
@RequestMapping("/auth")
public class LoginController {
    @Autowired
    private LoginService loginService;
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private UserService userService;

    @Autowired
    private OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;

    @Autowired
    private OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login (@RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(loginService.signin(loginRequest));
    }

    // if token is confirmed will create new jwt after
    @GetMapping("/confirm")
    public ResponseEntity<LoginResponse> confirm(@RequestParam("token") String token, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        try {
            Integer userID = loginService.confirmToken(token, request, response);
            System.out.println("User: " + userID);

            // create new user authenticated
            UserDetails userDetails = userService.loadUserById(userID);
            System.out.println("UserDetails: " + userDetails);
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            authentication.setDetails((new WebAuthenticationDetailsSource().buildDetails(request)));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // call jwt authentication filter
            jwtAuthenticationFilter.successfulAuthentication(request,response,null, authentication);

            // get jwt token
            User jwt = userRepository.findTokenByUserID(userID);
            System.out.println("JWT: " + jwt.getToken());

            LoginResponse loginResponse = new LoginResponse(jwt.getToken());
            System.out.println("login response: " + loginResponse);

            return ResponseEntity.ok(loginResponse);
        } catch (Exception e) {
            System.out.println("Exception: " + e);
            throw e;
        }

    }

    @GetMapping("/error")
    public String handleError (@RequestParam("message") String message) {
       return "Error: " + message;
    }
    @PostMapping("/signin")
    public ResponseEntity<?> registerUser( @RequestBody SignUpRequest signUpRequest) throws BadRequestException {
        if(userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new BadRequestException("Email address already in use.");
        }

        // Creating user's account
        User user = new User();
        user.setName(signUpRequest.getName());
        user.setEmail(signUpRequest.getEmail());
        user.setAuthProvider(AuthProvider.local);

        User result = userRepository.save(user);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/user/me")
                .buildAndExpand(result.getUserID()).toUri();

        return ResponseEntity.created(location)
                .body(new ApiResponse(true, "User registered successfully@"));
    }
}
