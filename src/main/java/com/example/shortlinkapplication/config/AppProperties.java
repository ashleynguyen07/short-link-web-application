package com.example.shortlinkapplication.config;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "app")
@Configuration
@Data
public class AppProperties {
    private final Auth auth = new Auth();
    private final OAuth2 oAuth2 = new OAuth2();
    public static class Auth {
        private String tokenSecret;
        private long tokenExpiration;

        public String getTokenSecret() {
            return tokenSecret;
        }

        public void setTokenSecret(String tokenSecret) {
            this.tokenSecret = tokenSecret;
        }

        public long getTokenExpiration() {
            return tokenExpiration;
        }

        public void setTokenExpiration(long tokenExpiration) {
            this.tokenExpiration = tokenExpiration;
        }
    }

    @Getter
    public static class OAuth2 {
        private List<String> authorizedRedirectUri = new ArrayList<>();

        public OAuth2 authorizedRedirectUri(List<String> authorizedRedirectUri) {
            this.authorizedRedirectUri = authorizedRedirectUri;
            return this;
        }
    }
}
