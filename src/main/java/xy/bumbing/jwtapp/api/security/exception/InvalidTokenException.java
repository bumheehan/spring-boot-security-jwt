package xy.bumbing.jwtapp.api.security.exception;

import org.springframework.security.core.AuthenticationException;

public class InvalidTokenException extends AuthenticationException {
    public InvalidTokenException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
