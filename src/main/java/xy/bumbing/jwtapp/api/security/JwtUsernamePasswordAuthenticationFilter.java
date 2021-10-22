package xy.bumbing.jwtapp.api.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import xy.bumbing.jwtapp.api.security.dto.ErrorResponse;
import xy.bumbing.jwtapp.api.security.dto.TokenResponse;
import xy.bumbing.jwtapp.api.security.exception.ParameterNotFoundException;
import xy.bumbing.jwtapp.api.security.exception.TokenNotFoundException;
import xy.bumbing.jwtapp.api.security.service.AuthService;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class JwtUsernamePasswordAuthenticationFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;
    private final RequestMatcher loginURLMatcher;
    private final String usernameParameterName;
    private final String passwordParameterName;
    private final String refreshTokenParameterName;
    private final AuthService authService;

    private static final String TOKEN_TYPE = "Bearer";

    public JwtUsernamePasswordAuthenticationFilter(String loginURL, String loginMethod, String usernameParameterName, String passwordParameterName, String refreshTokenParameterName, AuthService authService) {
        this.loginURLMatcher = new AntPathRequestMatcher(loginURL, loginMethod);
        this.usernameParameterName = usernameParameterName;
        this.passwordParameterName = passwordParameterName;
        this.refreshTokenParameterName = refreshTokenParameterName;
        this.authService = authService;
        this.objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            //Login url 체크
            if ((!this.loginURLMatcher.matches(request))) {
                log.debug("일반 URL 요청");
                this.processNormalUrl(request);
                this.doFilter(request, response, filterChain);
                return;
            }
            log.debug("login URL 요청");

            Optional<String> tokenOptional = this.getTokenByRequest(request);
            TokenResponse tokenResponse;
            if (tokenOptional.isPresent()) {
                //토큰 로그인
                tokenResponse = this.processTokenLogin(request, tokenOptional.get());
            } else {
                // 아이디 비밀번호로그인
                tokenResponse = this.processBasicLogin(request);
            }

            //response
            this.printMessage(response, tokenResponse);

        } catch (AuthenticationException e) {
            log.error("인증 에러 ", e);
            this.printErrorMessage(request, response, e, HttpStatus.UNAUTHORIZED);
        }

    }

    private TokenResponse processTokenLogin(HttpServletRequest request, String token) throws IOException {

        Authentication authenticationToken = this.authService.authenticate(token, true);
        HashMap requestValue = this.objectMapper.readValue(request.getReader(), HashMap.class);
        String refreshToken = (String) requestValue.get(this.refreshTokenParameterName);
        if (refreshToken != null) {
            return this.authService.loginRefreshToken(authenticationToken.getPrincipal().toString(), (Map<String, Object>) authenticationToken.getDetails(), refreshToken);
        } else {
            throw new TokenNotFoundException("not found refresh token", null);
        }

    }

    private TokenResponse processBasicLogin(HttpServletRequest request) throws IOException {
        HashMap requestValue = this.objectMapper.readValue(request.getReader(), HashMap.class);
        Object username = requestValue.get(this.usernameParameterName);
        Object password = requestValue.get(this.passwordParameterName);
        if (username == null || password == null) {
            //401
            throw new ParameterNotFoundException("not found username or password", null);
        } else {
            return this.authService.login((String) username, (String) password);
        }
    }

    private void processNormalUrl(HttpServletRequest request) throws IOException {
        Optional<String> tokenOptional = this.getTokenByRequest(request);
        if (tokenOptional.isPresent()) {
            String token = tokenOptional.get();
            Authentication authentication = this.authService.authenticate(token, false);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        //미인증넘김
//        else {
//            throw new TokenNotFoundException("not found access token", null);
//        }
    }

    private Optional<String> getTokenByRequest(HttpServletRequest request) {
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (token != null) {
            if (token.startsWith(TOKEN_TYPE)) {
                return Optional.of(token.substring(7));
            }
        }
        return Optional.empty();
    }


    public void printErrorMessage(HttpServletRequest request, HttpServletResponse response, Exception e, HttpStatus httpStatus) throws IOException {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .error(e.getClass().getName())
                .message(e.getMessage())
                .path(request.getRequestURI())
                .timestamp(Instant.now())
                .status(httpStatus.value())
                .build();
        response.setContentType("application/json;charset=utf-8");
        response.sendError(errorResponse.getStatus(), this.objectMapper.writeValueAsString(errorResponse));
    }

    public void printMessage(HttpServletResponse response, TokenResponse tokenResponse) throws IOException {
        response.setContentType("application/json;charset=utf-8");
        response.getOutputStream().println(this.objectMapper.writeValueAsString(tokenResponse));
    }
}
