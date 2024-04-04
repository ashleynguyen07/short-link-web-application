package com.example.shortlinkapplication.config;

import com.example.shortlinkapplication.repository.UserRepository;
import com.example.shortlinkapplication.security.TokenAuthenticationFilter;
import com.example.shortlinkapplication.security.oauth.CustomOAuth2UserService;
import com.example.shortlinkapplication.security.oauth.HttpCookieOAuth2AuthorizationRequestRepository;
import com.example.shortlinkapplication.security.oauth.OAuth2AuthenticationFailureHandler;
import com.example.shortlinkapplication.security.oauth.OAuth2AuthenticationSuccessHandler;
import com.example.shortlinkapplication.service.UserService;
import java.util.Collections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Autowired
  private CustomOAuth2UserService oAuth2UserService;
  @Autowired
  private UserService userService;
  @Autowired
  @Lazy
  private OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
  @Autowired
  @Lazy
  private OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;

  @Autowired
  private UserRepository userRepository;

  @Bean
  public TokenAuthenticationFilter tokenAuthenticationFilter() {
    return new TokenAuthenticationFilter();
  }

  @Bean
  public HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository() {
    return new HttpCookieOAuth2AuthorizationRequestRepository();
  }

  @Bean
  public DaoAuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
    provider.setUserDetailsService(userService);
    return provider;
  }

  @Bean
  public AuthenticationManager authenticationManager() {
    return new ProviderManager(Collections.singletonList(authenticationProvider()));
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .csrf(AbstractHttpConfigurer::disable)
        .formLogin(AbstractHttpConfigurer::disable)
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(requests -> requests
            .requestMatchers("/auth/**", "/oauth2/**", "/signin", "/dashboard/**", "/public/**",
                "/**", "/profile/**").permitAll()
            .anyRequest().authenticated())
        .oauth2Login(oauth -> oauth
            .authorizationEndpoint(auth -> auth.baseUri("/oauth2/authorize")
                .authorizationRequestRepository(httpCookieOAuth2AuthorizationRequestRepository())
            )
            .redirectionEndpoint(endpoint -> endpoint.baseUri("/oauth2/callback/*"))
            .userInfoEndpoint(userInfo -> userInfo.userService(oAuth2UserService))
            .successHandler(oAuth2AuthenticationSuccessHandler)
            .failureHandler(oAuth2AuthenticationFailureHandler)
        )
        .addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }


}