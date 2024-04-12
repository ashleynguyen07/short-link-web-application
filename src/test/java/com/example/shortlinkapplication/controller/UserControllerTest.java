package com.example.shortlinkapplication.controller;

import static org.mockito.Mockito.when;

import com.example.shortlinkapplication.dto.profile.DeleteProfileRequest;
import com.example.shortlinkapplication.dto.profile.UpdateProfileRequest;
import com.example.shortlinkapplication.entity.User;
import com.example.shortlinkapplication.repository.UserRepository;
import com.example.shortlinkapplication.security.UserPrincipal;
import com.example.shortlinkapplication.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(UserController.class)
@ExtendWith(MockitoExtension.class)
@Slf4j
class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private ObjectMapper objectMapper;
  @MockBean
  private UserRepository userRepository;
  @MockBean
  private ProjectController projectController;
  @MockBean
  private UserService userService;
  private UserPrincipal userPrincipal;
  private User user;

  @BeforeEach
  void setUp() {
    user = new User();
    user.setUserID(1);
    user.setEmail("nguyentramy.se@gmail.com");

    // Mock authentication
    userPrincipal = new UserPrincipal(user.getUserID(), user.getEmail());
    log.info("User Principal: {}", userPrincipal);
    Authentication authentication = new UsernamePasswordAuthenticationToken(userPrincipal, null,
        null);
    log.info("Authentication: {}", authentication);
    SecurityContext securityContext = SecurityContextHolder.getContext();
    securityContext.setAuthentication(authentication);

  }

  /**
   * JUnit test GET request - getCurrentUser method with exist user then return user object
   */
  @Test
  @DisplayName("getCurrentUser() with exist user")
  void givenExistUser_whenGetCurrentUser_thenReturnUserObject() throws Exception {
    when(userRepository.findById(user.getUserID())).thenReturn(Optional.of(user));

    // GET request
    mockMvc.perform(MockMvcRequestBuilders.get("/user/me")
            .with(SecurityMockMvcRequestPostProcessors.user(userPrincipal)))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.userID").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("nguyentramy.se@gmail.com"));
  }

  /**
   * JUnit test PUT request - updateUserProfile method return updated user object
   */
  @Test
  @DisplayName("updateUserProfile()")
  void givenUpdateProfileRequest_whenUpdateUserProfile_thenReturnUserObject() throws Exception {
    when(projectController.getUser(userPrincipal)).thenReturn(user);
    log.info("User: {}", user);

    UpdateProfileRequest request = new UpdateProfileRequest();
    request.setEmail("meme@gmail.com");

    user.setEmail(request.getEmail());

    when(userService.updateUserProfile(request, user.getUserID())).thenReturn(user);
    log.info("User updated: {}", user);

    // PUT request
    mockMvc.perform(MockMvcRequestBuilders.put("/update-profile")
            .with(SecurityMockMvcRequestPostProcessors.user(userPrincipal))
            .with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.userID").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("meme@gmail.com"));
  }

  /**
   * JUnit test DELETE request - deleteUserProfile method Which correct verify string then return
   * string
   */
  @Test
  @DisplayName("deleteUserProfile()")
  void givenDeleteProfileRequest_whenDeleteUserProfile_thenReturnSuccessString()
      throws Exception {
    when(projectController.getUser(userPrincipal)).thenReturn(user);

    DeleteProfileRequest request = new DeleteProfileRequest();
    request.setVerify("confirm delete account");
    when(userService.deleteUserProfile(request, user.getUserID())).thenReturn(
        "Delete account success!");

    mockMvc.perform(MockMvcRequestBuilders.delete("/delete-profile")
            .with(SecurityMockMvcRequestPostProcessors.csrf())
            .with(SecurityMockMvcRequestPostProcessors.user(userPrincipal))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().string("Delete account success!"));
  }
}