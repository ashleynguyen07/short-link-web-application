package com.example.shortlinkapplication.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class EmailValidatorTest {

  private EmailValidator emailValidator;

  @BeforeEach
  void setUp() {
    emailValidator = new EmailValidator();
  }

  /***
   * JUnit test - test method - validate email return true
   */
  @Test
  @DisplayName("test() valid email address")
  void validEmailAddress() {
    assertTrue(emailValidator.test("test@example.com"));
    assertTrue(emailValidator.test("test.user@example.com"));
    assertTrue(emailValidator.test("test_user123@example.com"));
    assertTrue(emailValidator.test("test123@example.co.uk"));
  }

  /***
   * JUnit test - test method - validate email return false
   */
  @Test
  @DisplayName("test() invalid email address")
  void invalidEmailAddress() {
    assertFalse(emailValidator.test("invalid.email.com"));
    assertFalse(emailValidator.test("@example.com"));
  }
}