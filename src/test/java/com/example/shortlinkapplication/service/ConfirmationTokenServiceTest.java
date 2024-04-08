package com.example.shortlinkapplication.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.example.shortlinkapplication.entity.ConfirmationToken;
import com.example.shortlinkapplication.repository.ConfirmationTokenRepository;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
class ConfirmationTokenServiceTest {

  @Mock
  private ConfirmationTokenRepository confirmationTokenRepository;
  @InjectMocks
  private ConfirmationTokenService confirmationTokenService;
  private ConfirmationToken confirmationToken;

  @BeforeEach
  void setUp() {
    confirmationToken = new ConfirmationToken();
    confirmationToken.setToken("testToken");
  }

  /**
   * JUnit test save confirmation token method
   */
  @Test
  void saveConfirmationToken() {
    confirmationTokenService.saveConfirmationToken(confirmationToken);
    verify(confirmationTokenRepository, times(1)).save(confirmationToken);
  }

  /**
   * JUnit test get token method
   */
  @Test
  @DisplayName("getToken()")
  void givenToken_whenGetToken_thenReturnConfirmationTokenObject() {
    String token = "testToken";
    given(confirmationTokenRepository.findByToken(token)).willReturn(confirmationToken);
    confirmationTokenService.getToken(confirmationToken.getToken());

    verify(confirmationTokenRepository, times(1)).findByToken(token);
    assertThat(confirmationToken.getToken()).isEqualTo(token);
  }

  /**
   * JUnit test set confirmed at
   */
  @Test
  void setConfirmedAt() {
    String token = "testToken";
    confirmationTokenService.setConfirmedAt(token);
    verify(confirmationTokenRepository).updateConfirmedAt(eq(token), any(LocalDateTime.class));
  }
}