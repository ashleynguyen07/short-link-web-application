package com.example.shortlinkapplication.service.url;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class BaseConversion {

  private static final Logger logger = LoggerFactory.getLogger(BaseConversion.class);
  private static final String BASE62 = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
  private static final char[] allowedCharacters = BASE62.toCharArray();
  public static final int BASE = allowedCharacters.length;

  public String encode(String id) {
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      byte[] hash = digest.digest(id.getBytes(StandardCharsets.UTF_8));

      BigInteger number = new BigInteger(1, hash);

      StringBuilder encodeString = new StringBuilder();
      while (number.compareTo(BigInteger.ZERO) > 0) {
        int remainder = number.mod(BigInteger.valueOf(BASE)).intValue();
        encodeString.insert(0, BASE62.charAt(remainder));
        number = number.divide(BigInteger.valueOf(BASE));
      }

      logger.info("Short url: {}", encodeString);
      return encodeString.toString();

    } catch (NoSuchAlgorithmException e) {
      throw new IllegalArgumentException(e);
    }
  }
  
}
