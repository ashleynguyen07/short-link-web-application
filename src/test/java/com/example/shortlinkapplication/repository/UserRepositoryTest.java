package com.example.shortlinkapplication.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.shortlinkapplication.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class UserRepositoryTest {

  @Autowired
  private UserRepository userRepository;
  private User user;

  @BeforeEach
  void setUp() {
    user = new User();
    user.setName("My");
    user.setEmail("nguyentramy.se@gmail.com");
    userRepository.save(user);
  }

  /**
   * JUnit test findByEmail method
   */
  @Test
  @DisplayName("findByEmail()")
  void givenEmail_whenFindByEmail_thenReturnUserObject() {
    User foundUser = userRepository.findByEmail(user.getEmail());
    assertThat(foundUser).isNotNull();
  }

  /**
   * JUnit test existByEmail method which return true
   */
  @Test
  @DisplayName("existsByEmail()")
  void givenEmail_whenExistsByEmail_thenReturnTrue() {
    Boolean foundUser = userRepository.existsByEmail(user.getEmail());
    assertThat(foundUser).isTrue();
  }
}