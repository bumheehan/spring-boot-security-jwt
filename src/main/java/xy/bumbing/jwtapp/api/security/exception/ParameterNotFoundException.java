package xy.bumbing.jwtapp.api.security.exception;

import org.springframework.security.core.AuthenticationException;

public class ParameterNotFoundException extends AuthenticationException {
    public ParameterNotFoundException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
