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
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LoginService {
    private final EmailValidator emailValidator;
    @Autowired
    private ConfirmationTokenService confirmationTokenService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ConfirmationTokenRepository confirmationTokenRepository;
    @Autowired
    private EmailSender emailSender;
    @Autowired
    private JWTService jwtService;

    // Regex to validate email - using test() method in EmailValidator
    public LoginResponse signin(LoginRequest loginRequest) {
        boolean isValidEmail = emailValidator.test(loginRequest.getEmail());
        if (!isValidEmail) {
            throw new IllegalArgumentException("Invalid email!");
        }
        User user = userRepository.findByEmail(loginRequest.getEmail());
        if (user == null) {
            throw new IllegalArgumentException("No user registered! Please Sign Up!");
        }
        LoginResponse loginResponse = createToken(user);
        String link = "http://localhost:8081/signin/confirm?token=" + loginResponse.getToken();
        emailSender.send(
                loginRequest.getEmail(),
                buildEmail(link));
        return loginResponse;
    }

    // create confirmation token
    public LoginResponse createToken(User user) {
        var jwt = jwtService.generateToken(user);
        System.out.println("Print JWT: " + jwt);
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setToken(jwt);
        ConfirmationToken confirmationToken = new ConfirmationToken(
                jwt,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15),
                user);
        System.out.println(confirmationToken);
        confirmationTokenService.saveConfirmationToken(confirmationToken);
        return loginResponse;
    }

    // validate confirm token - update confirmed at column
    // using @transactional for update db
    @Transactional
    public Boolean confirmToken(String token, HttpServletRequest request) {
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
        redirectToHome(confirmationToken.getUserID().getUserID());
        return true;
    }
    private void redirectToHome(Integer userID) {
        // Construct the redirect URL dynamically
        String redirectUrl = "/home/" + userID;
        try {
            HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getResponse();
            response.sendRedirect(redirectUrl);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to redirect to " + redirectUrl);
        }
    }

    @Transactional
    private String buildEmail(String link) {
        return "<p>Hello,</p>"
                + "<p>Please click on the link below to verify your email and complete the sign-in process:</p>"
                + "<a href=\"" + link + "\">Verify Email</a>"
//                + link
                + "<p>Thank you!</p>";
    }


}
