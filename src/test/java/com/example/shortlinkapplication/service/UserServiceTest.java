package com.example.shortlinkapplication.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.example.shortlinkapplication.dto.profile.DeleteProfileRequest;
import com.example.shortlinkapplication.dto.profile.UpdateProfileRequest;
import com.example.shortlinkapplication.entity.User;
import com.example.shortlinkapplication.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @Mock
  private UserRepository userRepository;
  @InjectMocks
  private UserService userService;
  private User user;
  private UpdateProfileRequest updateProfileRequest;
  private DeleteProfileRequest deleteProfileRequest;

  @BeforeEach
  void setUp() {
    user = new User();
    user.setUserID(1);
    user.setName("My");
    user.setEmail("nguyentramy.se@gmail.com");

    updateProfileRequest = new UpdateProfileRequest();
    updateProfileRequest.setName("Hung");
    updateProfileRequest.setEmail("hoangvuhung@gmail.com");

    deleteProfileRequest = new DeleteProfileRequest();
    deleteProfileRequest.setVerify("confirm delete account");
  }

  /**
   * JUnit test loadUserByUsername method
   */
  @Test
  @DisplayName("loadUserByUsername()")
  void givenExistingUsername_whenLoadUserByUsername_thenReturnUserDetails() {
    given(userRepository.findByEmail(user.getEmail())).willReturn(user);
    UserDetails userDetails = userService.loadUserByUsername(user.getEmail());
    assertThat(userDetails).isNotNull();
    assertThat(userDetails.getUsername()).isEqualTo(user.getEmail());
  }

  /**
   * JUnit test loadUserByUsername method which username is non-exist
   */
  @Test
  @DisplayName("loadUserByUsername which username is non-exist")
  void givenNonExistUsername_whenLoadUserByUsername_thenThrowsException() {
    String nonExistEmail = "belu772001@gmail.com";
    given(userRepository.findByEmail(nonExistEmail)).willReturn(null);
    assertThrows(UsernameNotFoundException.class, () -> {
      userService.loadUserByUsername(nonExistEmail);
    });
  }

  /**
   * JUnit test loadUserById method
   */
  @Test
  @DisplayName("loadUserById()")
  void givenUserID_whenLoadUserById_thenReturnUserDetails() {
    given(userRepository.findById(user.getUserID())).willReturn(Optional.ofNullable(user));
    UserDetails userDetails = userService.loadUserById(user.getUserID());
    assertThat(userDetails).isNotNull();
    assertThat(userDetails.getUsername()).isEqualTo(user.getEmail());
  }

  /**
   * JUnit test loadUserById method with non-exist user id
   */
  @Test
  @DisplayName("loadUserById with non-exist userID")
  void givenNonExistUserID_whenLoadUserById_thenReturnException() {
    Integer id = 10;
    given(userRepository.findById(id)).willReturn(Optional.empty());
    assertThrows(UsernameNotFoundException.class, () -> {
      userService.loadUserById(id);
    });
  }

  /**
   * JUnit test updateUserProfile method
   */
  @Test
  @DisplayName("updateUserProfile()")
  void givenUpdateRequestAndUserID_whenUpdateUserProfile_thenReturnUserObject() {
    given(userRepository.findById(user.getUserID())).willReturn(Optional.ofNullable(user));
    User expected = userService.updateUserProfile(updateProfileRequest, user.getUserID());
    assertThat(expected).isNotNull();
    assertThat(expected.getName()).isEqualTo(updateProfileRequest.getName());
    assertThat(expected.getEmail()).isEqualTo(updateProfileRequest.getEmail());

    // must have verified save -> after update user must be saved to complete the process.
    verify(userRepository, times(1)).save(user);
  }

  /**
   * JUnit test updateUserMethod method with non-existing user
   */
  @Test
  @DisplayName("updateUserMethod() with non-existing user")
  void givenUpdateRequestAndUserID_whenUpdateUserProfile_thenReturnNull() {
    given(userRepository.findById(user.getUserID())).willReturn(Optional.empty());

    User updatedUser = userService.updateUserProfile(updateProfileRequest, user.getUserID());

    assertThat(updatedUser).isNull();
  }

  /**
   * JUnit test deleteUserProfile method
   */
  @Test
  @DisplayName("deleteUserProfile()")
  void givenDeleteRequestAndUserID_whenDeleteUserProfile_thenReturnNothing() {
    given(userRepository.findById(user.getUserID())).willReturn(Optional.ofNullable(user));
    willDoNothing().given(userRepository).deleteById(user.getUserID());

    userService.deleteUserProfile(deleteProfileRequest, user.getUserID());

    assertThat(deleteProfileRequest.getVerify()).isEqualTo("confirm delete account");
    verify(userRepository, times(1)).deleteById(user.getUserID());
  }

  /**
   * JUnit test deleteUserProfile method when verify string is incorrect
   */
  @Test
  @DisplayName("deleteUserProfile() when verify string is incorrect")
  void givenDeleteRequestWithIncorrectVerifyString_whenDeleteUserProfile_thenUserIsNotDeleted() {
    DeleteProfileRequest request = new DeleteProfileRequest();
    request.setVerify("incorrect verify string");

    given(userRepository.findById(user.getUserID())).willReturn(Optional.ofNullable(user));

    String result = userService.deleteUserProfile(request, user.getUserID());

    // Xác minh deleteById không được gọi
    verify(userRepository, times(0)).deleteById(user.getUserID());

    // Kiểm tra xem kết quả có phải là null không
    assertNull(result);
  }

  /**
   * JUnit test deleteUserProfile method with non-existing user
   */
  @Test
  @DisplayName("deleteUserProfile() with non-existing user")
  void givenDeleteRequestAndUserID_whenDeleteUserProfile_thenReturnNull() {
    int nonExistingUser = 999;
    given(userRepository.findById(nonExistingUser)).willReturn(Optional.empty());

    userService.deleteUserProfile(deleteProfileRequest, nonExistingUser);

    assertThat(userService.deleteUserProfile(deleteProfileRequest, nonExistingUser)).isNull();
  }
}
