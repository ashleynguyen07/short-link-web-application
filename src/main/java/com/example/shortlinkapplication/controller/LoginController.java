package com.example.shortlinkapplication.controller;

import com.example.shortlinkapplication.dto.LoginRequest;
import com.example.shortlinkapplication.dto.LoginResponse;
import com.example.shortlinkapplication.security.TokenProvider;
import com.example.shortlinkapplication.service.LoginService;
import com.example.shortlinkapplication.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class LoginController {

  private final LoginService loginService;
  private final TokenProvider tokenProvider;
  private final UserService userService;

  @PostMapping("/login")
  public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest,
      HttpServletRequest request) {
    return ResponseEntity.ok(loginService.signin(loginRequest, request));
  }

  @GetMapping("/logout")
  public ResponseEntity<String> logout() {
    SecurityContextHolder.clearContext();
    return ResponseEntity.ok("Logout successful");
  }

  // if token is confirmed will create new jwt after
  @GetMapping("/confirm")
  public ResponseEntity<LoginResponse> confirm(@RequestParam("token") String token,
      HttpServletRequest request, HttpServletResponse response) {
    Integer userID = loginService.confirmToken(token, request, response);

    // create new user authenticated
    UserDetails userDetails = userService.loadUserById(userID);
    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
        userDetails, null, userDetails.getAuthorities());
    authentication.setDetails((new WebAuthenticationDetailsSource().buildDetails(request)));

    SecurityContextHolder.getContext().setAuthentication(authentication);

    String jwt = tokenProvider.createToken(authentication);
    log.info("JWT: {}", jwt);

    LoginResponse loginResponse = new LoginResponse(jwt);

    // Add token to the Authorization header
    response.addHeader("Authorization", "Bearer " + jwt);

    return ResponseEntity.ok(loginResponse);
  }

}
