package com.example.shortlinkapplication.service;

import com.example.shortlinkapplication.entity.User;
import com.example.shortlinkapplication.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    @Autowired
    private UserRepository userRepository;

    // validate if user is null
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        System.out.println("loadUser: " + oAuth2User);
        String email = oAuth2User.getAttribute("email");
        if (email == null) {
            email = oAuth2User.getAttribute("login");
        }

        // check provider of social
        String provider = userRequest.getClientRegistration().getRegistrationId();
        String nameAttributeKey;
        if (provider.equals("google")) {
            nameAttributeKey = "email";
        } else if (provider.equals("github")) {
            nameAttributeKey = "login";
        } else {
            throw new IllegalArgumentException("Unsupported provider " + provider);
        }
        return new DefaultOAuth2User(Collections.emptySet(), oAuth2User.getAttributes(), nameAttributeKey);
        }
}
