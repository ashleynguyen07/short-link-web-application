package com.example.shortlinkapplication.security;

import com.example.shortlinkapplication.config.AppProperties;
import com.example.shortlinkapplication.entity.User;
import com.example.shortlinkapplication.repository.UserRepository;
import com.example.shortlinkapplication.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import java.security.Key;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class TokenProvider {

  private static final Logger logger = LoggerFactory.getLogger(TokenProvider.class);

  private AppProperties appProperties;

  @Autowired
  private UserRepository userRepository;
  @Autowired
  private UserService userService;

  public TokenProvider(AppProperties appProperties) {
    this.appProperties = appProperties;
  }

  public String createToken(Authentication authentication) {
    UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

    Date now = new Date();
    Date expiryDate = new Date(now.getTime() + appProperties.getAuth().getTokenExpiration());

    String jwt = Jwts.builder()
        .setSubject(Long.toString(userPrincipal.getId()))
        .setIssuedAt(new Date())
        .setExpiration(expiryDate)
        .signWith(getSigninKey(), SignatureAlgorithm.HS256)
        .compact();

    User user = userRepository.findById(userPrincipal.getId()).orElseThrow(
        () -> new UsernameNotFoundException("User not found with id: " + userPrincipal.getId()));
    user.setToken(jwt);
    User updatedUser = userRepository.save(user);

    return jwt;
  }

  private Key getSigninKey() {
    byte[] key = Decoders.BASE64.decode(appProperties.getAuth().getTokenSecret());
    return Keys.hmacShaKeyFor(key);
  }

  public Integer getUserIdFromToken(String token) {
    Claims claims = Jwts.parserBuilder()
        .setSigningKey(getSigninKey())
        .build()
        .parseClaimsJws(token)
        .getBody();

    return Integer.parseInt(claims.getSubject());
  }

  public boolean validateToken(String authToken) {
    try {
      Jwts.parserBuilder()
          .setSigningKey(getSigninKey());
      return true;
    } catch (SignatureException ex) {
      logger.error("Invalid JWT signature");
    } catch (MalformedJwtException ex) {
      logger.error("Invalid JWT token");
    } catch (ExpiredJwtException ex) {
      logger.error("Expired JWT token");
    } catch (UnsupportedJwtException ex) {
      logger.error("Unsupported JWT token");
    } catch (IllegalArgumentException ex) {
      logger.error("JWT claims string is empty.");
    }
    return false;
  }
}
