package com.example.shortlinkapplication.service;

import com.example.shortlinkapplication.entity.ConfirmationToken;
import com.example.shortlinkapplication.repository.ConfirmationTokenRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConfirmationTokenService {

  private final ConfirmationTokenRepository confirmationTokenRepository;

  // save token
  public void saveConfirmationToken(ConfirmationToken token) {
    confirmationTokenRepository.save(token);
  }

  // get token
  public ConfirmationToken getToken(String token) {
    return confirmationTokenRepository.findByToken(token);
  }

  // set confirmed at
  public void setConfirmedAt(String token) {
    confirmationTokenRepository.updateConfirmedAt(token, LocalDateTime.now());
  }
}
