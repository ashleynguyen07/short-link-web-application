package com.example.shortlinkapplication.security.oauth;

import com.example.shortlinkapplication.entity.AuthProvider;
import com.example.shortlinkapplication.entity.User;
import com.example.shortlinkapplication.repository.UserRepository;
import com.example.shortlinkapplication.security.UserPrincipal;
import com.example.shortlinkapplication.security.oauth.user.OAuth2UserInfo;
import com.example.shortlinkapplication.security.oauth.user.OAuth2UserInfoFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

  @Autowired
  private final UserRepository userRepository;

  @Override
  public OAuth2User loadUser(OAuth2UserRequest userRequest) {
    // loadUser will get access token
    OAuth2User oAuth2User = super.loadUser(userRequest);

    try {
      return processOAuth2User(userRequest, oAuth2User);
    } catch (AuthenticationException ex) {
      throw ex;
    } catch (Exception ex) {
      throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
    }
  }

  // validate if user is null
  private OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oAuth2User) {
    OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(
        userRequest.getClientRegistration().getRegistrationId(), oAuth2User.getAttributes());
    if (ObjectUtils.isEmpty(oAuth2UserInfo.getEmail())) {
      throw new IllegalArgumentException("Email not found from OAuth2 Provider");
    }
    System.out.println(oAuth2UserInfo.getEmail());
    User user = userRepository.findByEmail(oAuth2UserInfo.getEmail());
    System.out.println(user);
    if (user != null) {
      if (!user.getAuthProvider()
          .equals(AuthProvider.valueOf(userRequest.getClientRegistration().getRegistrationId()))) {
        throw new IllegalArgumentException(
            "Please use your " + user.getAuthProvider() + " account to login!");
      }
      System.out.println("Updating user.....");
      user = updateExistingUser(user, oAuth2UserInfo);
    } else {
      System.out.println("Registering user......");
      user = registerNewUser(userRequest, oAuth2UserInfo);
    }
    return UserPrincipal.create(user, oAuth2User.getAttributes());
  }

  private User registerNewUser(OAuth2UserRequest userRequest, OAuth2UserInfo oAuth2UserInfo) {
    User user = new User();
    user.setAuthProvider(
        AuthProvider.valueOf(userRequest.getClientRegistration().getRegistrationId()));
    user.setProviderId(oAuth2UserInfo.getId());
    user.setName(oAuth2UserInfo.getName());
    user.setEmail(oAuth2UserInfo.getEmail());
    return userRepository.save(user);
  }

  private User updateExistingUser(User existingUser, OAuth2UserInfo oAuth2UserInfo) {
    existingUser.setName(oAuth2UserInfo.getName());
    return userRepository.save(existingUser);
  }
}
