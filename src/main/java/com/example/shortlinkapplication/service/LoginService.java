package com.example.shortlinkapplication.service;

import com.example.shortlinkapplication.dto.LoginRequest;
import com.example.shortlinkapplication.dto.LoginResponse;
import com.example.shortlinkapplication.email.EmailSender;
import com.example.shortlinkapplication.entity.ConfirmationToken;
import com.example.shortlinkapplication.entity.User;
import com.example.shortlinkapplication.repository.ConfirmationTokenRepository;
import com.example.shortlinkapplication.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LoginService {

  private final EmailValidator emailValidator;
  private final ConfirmationTokenService confirmationTokenService;
  private final UserRepository userRepository;
  private final EmailSender emailSender;

  @Autowired
  public LoginService(EmailValidator emailValidator,
      ConfirmationTokenService confirmationTokenService, UserRepository userRepository,
      ConfirmationTokenRepository confirmationTokenRepository, EmailSender emailSender
  ) {
    this.emailValidator = emailValidator;
    this.confirmationTokenService = confirmationTokenService;
    this.userRepository = userRepository;
    this.emailSender = emailSender;
  }

  // Regex to validate email - using test() method in EmailValidator
  public LoginResponse signin(LoginRequest loginRequest, HttpServletRequest request) {
    boolean isValidEmail = emailValidator.test(loginRequest.getEmail());
    if (!isValidEmail) {
      throw new IllegalArgumentException("Invalid email!");
    }
    User user = userRepository.findByEmail(loginRequest.getEmail());
    if (user == null) {
      throw new IllegalArgumentException("No user registered! Please Sign Up!");
    }
    LoginResponse loginResponse = createToken(user);
    String serverUrl = request.getScheme() + "://" + request.getServerName();
    if (("http".equals(request.getScheme()) && request.getServerPort() != 80) || (
        "https".equals(request.getScheme()) && request.getServerPort() != 443)) {
      serverUrl += ":" + request.getServerPort();
    }
    String link = serverUrl + "/auth/confirm?token=" + loginResponse.getToken();
    emailSender.send(
        loginRequest.getEmail(),
        buildEmail(link));
    return loginResponse;
  }

  // create confirmation token
  public LoginResponse createToken(User user) {
    String token = UUID.randomUUID().toString();

    LoginResponse loginResponse = new LoginResponse(token);
    loginResponse.setToken(token);

    ConfirmationToken confirmationToken = new ConfirmationToken(
        token,
        LocalDateTime.now(),
        LocalDateTime.now().plusMinutes(15),
        user);
    confirmationTokenService.saveConfirmationToken(confirmationToken);
    return loginResponse;
  }

  // validate confirm token - update confirmed at column
  // using @transactional for update db
  @Transactional
  public Integer confirmToken(String token, HttpServletRequest request,
      HttpServletResponse response) {
    ConfirmationToken confirmationToken = confirmationTokenService.getToken(token);
    if (confirmationToken == null) {
      throw new IllegalStateException("token not found");
    }
    if (confirmationToken.getConfirmedAt() != null) {
      throw new IllegalStateException("email already confirmed");
    }
    LocalDateTime expiredAt = confirmationToken.getExpiredAt();
    if (expiredAt.isBefore(LocalDateTime.now())) {
      throw new IllegalStateException("token expired");
    }
    confirmationTokenService.setConfirmedAt(token);
    HttpSession session = request.getSession();
    session.setAttribute("userID", confirmationToken.getUserID());
    return confirmationToken.getUserID().getUserID();
  }

  public String buildEmail(String link) {
    return "<p>Hello,</p>"
        + "<p>Please click on the link below to verify your email and complete the sign-in process:</p>"
        //+ "<a href=\"" + link + "\">Verify Email</a>"
        + link
        + "<p>Thank you!</p>";
  }


}
