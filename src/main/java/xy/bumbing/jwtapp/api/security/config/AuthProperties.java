package xy.bumbing.jwtapp.api.security.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import xy.bumbing.jwtapp.api.security.utils.JwtUtils;

@Data
@Configuration
@ConfigurationProperties("security")
public class AuthProperties {

    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public JwtUtils getJwtUtils() {
        return new JwtUtils(this.getToken().getAccess().getSecret(), this.getToken().getRefresh().getSecret(), this.getToken().getAccess().getExpired(), this.getToken().getRefresh().getExpired());
    }

    private Token token;
    private Login login;

    @Data
    public static class Token {
        private SingleToken access;
        private SingleToken refresh;
    }

    @Data
    public static class SingleToken {
        private String secret;
        private Integer expired;
    }

    @Data
    public static class Login {
        private String url = "/api/auth/login";
        private String method = "POST";
        private String usernameParameter = "username";
        private String passwordParameter = "password";
        private String refreshTokenParameter = "refreshToken";
    }

}
