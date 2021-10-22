package xy.bumbing.jwtapp.api.security.exception;

import org.springframework.security.core.AuthenticationException;

public class AccountNotFoundException extends AuthenticationException {
    public AccountNotFoundException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
