package com.example.shortlinkapplication.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.shortlinkapplication.entity.ConfirmationToken;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class ConfirmationTokenRepositoryTest {

  @Autowired
  private ConfirmationTokenRepository repository;
  private ConfirmationToken confirmationToken;

  @BeforeEach
  void setUp() {
    confirmationToken = new ConfirmationToken();
    confirmationToken.setId(1);
    confirmationToken.setToken("sbaewuboiuaewfweihoeiwg");
    confirmationToken.setConfirmedAt(LocalDateTime.now());
    repository.save(confirmationToken);
  }

  /**
   * JUnit Test findByToken method
   */
  @Test
  @DisplayName("findByToken()")
  void givenToken_whenFindByToken_thenReturnConfirmationToken() {
    ConfirmationToken expectedConfirmationToken =
        repository.findByToken(confirmationToken.getToken());
    assertThat(expectedConfirmationToken).isNotNull();
    assertThat(expectedConfirmationToken.getId()).isEqualTo(confirmationToken.getId());
  }

  /**
   * JUnit test updateConfirmedAt method
   */
  @Test
  @DisplayName("updateConfirmedAt()")
  void givenConfirmationObject_whenUpdateConfirmedAt_thenReturnNothing() {
    ConfirmationToken updateConfirmationToken = new ConfirmationToken();
    updateConfirmationToken.setId(1);
    updateConfirmationToken.setToken("buaiefuhfeuwfheiuwfhowi");
    updateConfirmationToken.setConfirmedAt(LocalDateTime.now());
    repository.save(updateConfirmationToken);

    assertThat(updateConfirmationToken).isNotNull();
    assertThat(updateConfirmationToken.getToken()).isEqualTo("buaiefuhfeuwfheiuwfhowi");
  }
}