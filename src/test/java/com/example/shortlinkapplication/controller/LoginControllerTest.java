package com.example.shortlinkapplication.controller;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

import com.example.shortlinkapplication.dto.LoginRequest;
import com.example.shortlinkapplication.dto.LoginResponse;
import com.example.shortlinkapplication.entity.User;
import com.example.shortlinkapplication.repository.UserRepository;
import com.example.shortlinkapplication.security.TokenProvider;
import com.example.shortlinkapplication.service.LoginService;
import com.example.shortlinkapplication.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(LoginController.class)
class LoginControllerTest {

  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private ObjectMapper objectMapper;
  @MockBean
  private LoginService loginService;
  @MockBean
  private TokenProvider tokenProvider;
  @MockBean
  private UserService userService;
  @MockBean
  private UserRepository userRepository;
  private HttpServletRequest httpServletRequest;
  private HttpServletResponse response;
  private User user;
  private String token;

  @BeforeEach
  void setUp() {
    user = new User();
    user.setUserID(1);
    user.setEmail("meme@gmail.com");
    userRepository.save(user);

    token =
        "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwiaWF0IjoxNzEyNDA3MjIyLCJleHAiOjE3MTMyNzEyMjJ9"
            + ".-v64pWTfB3rk6ViIpBuV2itBlTqQuZbJ44V4tGF8Oog";
  }

  /**
   * JUnit test POST request - login method via email input
   */
  @Test
  @DisplayName("login()")
  @WithMockUser
  void givenEmail_whenLogin_thenReturnToken() throws Exception {
    LoginRequest request = new LoginRequest();
    request.setEmail("meme@gmail.com");

    LoginResponse response = new LoginResponse(token);

    when(loginService.signin(request, httpServletRequest)).thenReturn(response);

    mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
            .with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.token").value(token));
  }

  /**
   * JUnit test GET request - logout method
   */
  @Test
  @DisplayName("logout()")
  @WithMockUser
  void givenNothing_whenLogout_thenReturnSuccessString() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.get("/auth/logout"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().string("Logout successful"));

    mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
            .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  /**
   * JUnit test GET request - confirm method which verify via email
   */
  @Test
  @DisplayName("confirm()")
  @WithMockUser
  void givenStringCode_whenConfirm_thenReturnToken() throws Exception {
    String code = "najfuawfhaeufhuaefhuefh889u89";
    UserDetails userDetails = Mockito.mock(UserDetails.class);

    when(userDetails.getUsername()).thenReturn("test@example.com");
    when(userDetails.getAuthorities()).thenReturn(Collections.emptyList());

    when(loginService.confirmToken(code, httpServletRequest, response)).thenReturn(
        user.getUserID());
    when(userService.loadUserById(anyInt())).thenReturn(userDetails);
    when(tokenProvider.createToken(
        Mockito.any(UsernamePasswordAuthenticationToken.class))).thenReturn(token);

    mockMvc.perform(MockMvcRequestBuilders.get("/auth/confirm")
            .param("token", code))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.header().string("Authorization", "Bearer " + token));
  }
}