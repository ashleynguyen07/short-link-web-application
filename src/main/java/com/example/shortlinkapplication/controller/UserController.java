package com.example.shortlinkapplication.controller;

import com.example.shortlinkapplication.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("")
public class UserController {
    @CrossOrigin
    @GetMapping("/user/{userID}")
    @ResponseStatus(HttpStatus.OK)
    public Principal user(@PathVariable Integer userID, Principal user) {
        return user;
    }

    @CrossOrigin
    @GetMapping("/home/{userID}")
    public String home(@PathVariable Integer userID, HttpServletRequest request) {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("userID");
        return "Hello " + user.getEmail();
    }

}
