package com.example.shortlinkapplication.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.shortlinkapplication.dto.LoginRequest;
import com.example.shortlinkapplication.dto.LoginResponse;
import com.example.shortlinkapplication.email.EmailSender;
import com.example.shortlinkapplication.entity.ConfirmationToken;
import com.example.shortlinkapplication.entity.User;
import com.example.shortlinkapplication.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LoginServiceTest {

  @Mock
  private UserRepository userRepository;
  @Mock
  private EmailValidator emailValidator;
  @Mock
  private EmailSender emailSender;
  @Mock
  private ConfirmationTokenService confirmationTokenService;
  @Mock
  private HttpServletRequest request;
  @Mock
  private HttpServletResponse response;
  @Mock
  private HttpSession httpSession;
  @InjectMocks
  private LoginService loginService;
  private User user;
  private LoginRequest loginRequest;
  private ConfirmationToken confirmationToken;

  @BeforeEach
  void setUp() {
    user = new User();
    user.setUserID(1);
    user.setEmail("nguyentramy.se@gmail.com");

    loginRequest = new LoginRequest();
    loginRequest.setEmail("nguyentramy.se@gmail.com");

    confirmationToken = new ConfirmationToken();
    confirmationToken.setId(1);
    confirmationToken.setUserID(user);
    confirmationToken.setExpiredAt(LocalDateTime.now().plusHours(1));
  }

  /**
   * JUnit test sign in method
   */
  @Test
  @DisplayName("signIn()")
  void givenValidEmailAndUserExists_whenSignIn_thenReturnLoginResponse() {
    when(emailValidator.test(anyString())).thenReturn(true);
    when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(user);

    LoginResponse loginResponse = loginService.signin(loginRequest, request);
    user.setToken(loginResponse.getToken());

    assertThat(loginResponse).isNotNull();
    // verify that the email was sent
    verify(emailSender).send(eq(user.getEmail()), anyString());
  }

  /**
   * JUnit test signIn method which invalid email then return exception
   */
  @Test
  @DisplayName("signIn() with invalid email")
  void givenInValidEmail_whenSignIn_thenReturnException() {
    String invalidEmail = "belu772001";
    loginRequest.setEmail(invalidEmail);
    given(emailValidator.test(invalidEmail)).willReturn(false);

    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      loginService.signin(loginRequest, request);
    });
    assertEquals("Invalid email!", exception.getMessage());
  }

  /**
   * JUnit test signIn method which user is null then return exception
   **/
  @Test
  @DisplayName("signIn() with non-exist user")
  void givenNonExistEmail_whenSignIn_thenReturnException() {
    String nonExistEmail = "belu772001@gmail.com";
    loginRequest.setEmail(nonExistEmail);
    given(emailValidator.test(nonExistEmail)).willReturn(true);
    given(userRepository.findByEmail(nonExistEmail)).willReturn(null);

    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      loginService.signin(loginRequest, request);
    });
    assertEquals("No user registered! Please Sign Up!", exception.getMessage());
  }

  /**
   * JUnit test confirmToken method with non-expired token
   */
  @Test
  @DisplayName("confirmToken()")
  void givenToken_whenConfirmToken_thenReturnUserID() {
    when(confirmationTokenService.getToken(confirmationToken.getToken())).thenReturn(
        confirmationToken);
    when(request.getSession()).thenReturn(httpSession);
    Integer userID = loginService.confirmToken(confirmationToken.getToken(), request, response);
    assertThat(userID).isPositive();
  }

  /**
   * JUnit test confirmationToken method which confirmation is null then return exception
   */
  @Test
  @DisplayName("confirmationToken() which confirmation is null")
  void givenNullConfirmationToken_whenConfirmToken_thenReturnException() {
    given(confirmationTokenService.getToken(confirmationToken.getToken())).willReturn(null);

    Exception exception = assertThrows(IllegalStateException.class, () -> {
      loginService.confirmToken(confirmationToken.getToken(), request, response);
    });

    assertEquals("token not found", exception.getMessage());
  }

  /**
   * JUnit test confirmationToken method which email confirmed already then return exception
   */
  @Test
  @DisplayName("confirmationToken() which email confirmed")
  void givenConfirmedAt_whenConfirmToken_thenReturnException() {
    confirmationToken.setConfirmedAt(LocalDateTime.now());

    // stub
    given(confirmationTokenService.getToken(confirmationToken.getToken())).willReturn(
        confirmationToken);
    Exception exception = assertThrows(IllegalStateException.class, () -> {
      loginService.confirmToken(confirmationToken.getToken(), request, response);
    });
    assertEquals("email already confirmed", exception.getMessage());
  }

  /**
   * JUnit test confirmationToken method which token expired then return exception
   */
  @Test
  @DisplayName("confirmationToken() which token expired")
  void givenExpiredAt_whenConfirmToken_thenReturnException() {
    confirmationToken.setExpiredAt(LocalDateTime.now().minusHours(1));
    // stub
    given(confirmationTokenService.getToken(confirmationToken.getToken())).willReturn(
        confirmationToken);
    Exception exception = assertThrows(IllegalStateException.class, () -> {
      loginService.confirmToken(confirmationToken.getToken(), request, response);
    });
    assertEquals("token expired", exception.getMessage());
  }
}