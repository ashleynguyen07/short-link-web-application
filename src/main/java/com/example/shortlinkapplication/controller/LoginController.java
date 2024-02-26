package com.example.shortlinkapplication.controller;

import com.example.shortlinkapplication.dto.LoginRequest;
import com.example.shortlinkapplication.dto.LoginResponse;
import com.example.shortlinkapplication.entity.ConfirmationToken;
import com.example.shortlinkapplication.entity.User;
import com.example.shortlinkapplication.service.LoginService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/signin")
public class LoginController {
    @Autowired
    private LoginService loginService;

    @CrossOrigin
    @PostMapping
    public ResponseEntity<LoginResponse> login (@RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(loginService.signin(loginRequest));
    }

    @CrossOrigin
    @GetMapping(path = "confirm")
    public String confirm(@RequestParam("token") String token, HttpServletRequest request) {
        boolean isConfirmed = loginService.confirmToken(token, request);
        if (isConfirmed) {
            HttpSession session = request.getSession();
            User user = (User) session.getAttribute("userID");
            return "/home/" + user.getUserID();
        } else {
            return "/error";
        }
    }
}
