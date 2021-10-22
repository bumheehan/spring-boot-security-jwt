package xy.bumbing.jwtapp.api.security.service;

import org.springframework.security.core.Authentication;
import xy.bumbing.jwtapp.api.security.dto.TokenResponse;

import java.util.Map;

public interface AuthService {

    Authentication authenticate(String accessToken, boolean continueExpiredToken);

    TokenResponse login(String username, String password);

    TokenResponse loginRefreshToken(String username, Map<String, Object> payload, String refreshToken);
}
