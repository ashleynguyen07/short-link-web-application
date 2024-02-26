package com.example.shortlinkapplication.config;

import com.example.shortlinkapplication.entity.User;
import com.example.shortlinkapplication.repository.UserRepository;
import com.example.shortlinkapplication.service.UserService;
import com.example.shortlinkapplication.service.CustomOAuth2UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    @Autowired
    private CustomOAuth2UserService oAuth2UserService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private final UserService userService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        System.out.println("Start filer chain");
        http
                .csrf(AbstractHttpConfigurer::disable)
//                .csrf(c -> c.csrfTokenRepository((CookieCsrfTokenRepository.withHttpOnlyFalse())))
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers("/**").permitAll()
                        .requestMatchers("/signin").permitAll()
                        .anyRequest().authenticated())
                .oauth2Login(oauth -> oauth
                        .userInfoEndpoint(userInfo -> userInfo.userService(oAuth2UserService))
                        .successHandler(authenticationSuccessHandler())
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                ;

        System.out.println("Finish");
        return http.build();
    }
//    @Autowired
//    public void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.userDetailsService(userService);
//    }
//    @Bean
//    public AuthenticationProvider authenticationProvider() {
//        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
//        authenticationProvider.setUserDetailsService(userService);
//        return authenticationProvider;
//    }
    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return new AuthenticationSuccessHandler() {
            private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy(); // sendRedirect()

            @Override
            public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
                String targetUrl = determineTargetUrl(authentication, request);
                redirectStrategy.sendRedirect(request, response, targetUrl);
            }

            private String determineTargetUrl(Authentication authentication, HttpServletRequest request) {
                System.out.println("Start determine.....");
                Object principal = authentication.getPrincipal();
                String email;
                String name;

                if (principal instanceof DefaultOidcUser oidcUser) {
                    email = oidcUser.getAttribute("email");
                    name = oidcUser.getAttribute("name");
                } else if (principal instanceof DefaultOAuth2User oAuth2User) {
                    email = oAuth2User.getAttribute("login");
                    name = oAuth2User.getAttribute("name");
                } else {
                    throw new IllegalArgumentException("Authentication principal is not of a recognized type.");
                }
                System.out.println(email);

                User user = userRepository.findByEmail(email);
                if (user == null) {
                    System.out.println("Saving user,.....");
                    user = new User();
                    user.setEmail(email);
                    user.setName(name);
                    userRepository.save(user);
                    System.out.println(user);
                }
                HttpSession session = request.getSession();
                session.setAttribute("user", user);
                System.out.println(user);
                return "/home/" + user.getUserID();

            }
        };

    }
}
