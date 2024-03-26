package com.example.shortlinkapplication.controller;

import com.example.shortlinkapplication.entity.User;
import com.example.shortlinkapplication.repository.UserRepository;
import com.example.shortlinkapplication.security.CurrentUser;
import com.example.shortlinkapplication.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

  @Autowired
  private UserRepository userRepository;

  @CrossOrigin
  @GetMapping("user/me")
  public User getCurrentUser(@CurrentUser UserPrincipal userPrincipal) {
    return userRepository.findById(userPrincipal.getId())
        .orElseThrow(() -> new RuntimeException(String.valueOf(userPrincipal.getId())));
  }

}
