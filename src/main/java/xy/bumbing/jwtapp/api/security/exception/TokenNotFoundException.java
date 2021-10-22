package xy.bumbing.jwtapp.api.security.exception;

import org.springframework.security.core.AuthenticationException;

public class TokenNotFoundException extends AuthenticationException {
    public TokenNotFoundException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
