package com.example.shortlinkapplication.security;

import com.example.shortlinkapplication.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;

@AllArgsConstructor
@Data
public class UserPrincipal implements OAuth2User, UserDetails {
    private Integer id;
    private String email;
    private Map<String, Object> attributes;

    public UserPrincipal(Integer id, String email) {
        this.id = id;
        this.email = email;
    }

    public static UserPrincipal create(User user) {
        return new UserPrincipal(
                user.getUserID(),
                user.getEmail()
        );
    }
    public static UserPrincipal create(User user, Map<String, Object> attributes) {
        UserPrincipal userPrincipal = UserPrincipal.create(user);
        userPrincipal.setAttributes(attributes);
        System.out.println(userPrincipal);
        return userPrincipal;
    }


    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getName() {
        return String.valueOf(id);
    }
}
