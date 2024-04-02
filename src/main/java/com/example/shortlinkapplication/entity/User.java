package com.example.shortlinkapplication.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Collection;
import java.util.Collections;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Data
@Table(name = "user_account")
public class User implements UserDetails {

  @Id
  @Column(name = "userID")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer userID;

  @Column(name = "name", length = 20)
  private String name;

  @Column(name = "email", length = 32)
  private String email;

  @Enumerated(EnumType.STRING)
  @Column(name = "auth_provider")
  private AuthProvider authProvider;

  @Column(name = "provider_id")
  private String providerId;

  @Column(name = "token")
  private String token;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return Collections.emptyList();
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
}
