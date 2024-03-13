package com.example.shortlinkapplication.controller;

import com.example.shortlinkapplication.entity.User;
import com.example.shortlinkapplication.repository.UserRepository;
import com.example.shortlinkapplication.security.CurrentUser;
import com.example.shortlinkapplication.security.UserPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @CrossOrigin
    @GetMapping("user/me")
    public User getCurrentUser(@CurrentUser UserPrincipal userPrincipal) {
        System.out.println("getting current user");
        return userRepository.findById(userPrincipal.getId()).orElseThrow(() -> new RuntimeException(String.valueOf(userPrincipal.getId())));
    }

}
