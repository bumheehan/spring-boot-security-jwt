package xy.bumbing.jwtapp.api.security.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import xy.bumbing.jwtapp.api.security.dto.TokenDto;
import xy.bumbing.jwtapp.api.security.dto.TokenResponse;
import xy.bumbing.jwtapp.api.security.exception.AccountNotFoundException;
import xy.bumbing.jwtapp.api.security.exception.InvalidTokenException;
import xy.bumbing.jwtapp.api.security.utils.JwtUtils;

import java.util.Map;
import java.util.Optional;

public abstract class AbstractAuthService<T> implements AuthService {


    private JwtUtils jwtUtils;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public void setJwtUtils(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public Authentication authenticate(String accessToken, boolean continueExpiredToken) {
        try {
            JwtUtils.TokenValidDto tokenValidDto = this.jwtUtils.validateToken(accessToken, JwtUtils.TokenType.ACCESS);

            if (tokenValidDto.isExpired() && !continueExpiredToken) {
                throw new AccountExpiredException("expired token, refresh your access token ");
            }
            Map<String, Object> payload = tokenValidDto.getPayload();


            return getAuthenticationFromPayload(payload);
        } catch (IllegalArgumentException e) {
            throw new InvalidTokenException("invalid token", e);
        }
    }

    /**
     * DB에 있으면 true 없으면 false 반환
     *
     * @param username
     * @param refreshToken
     * @return
     */
    protected abstract boolean checkRefreshTokenFromDb(String username, String refreshToken);

    /**
     * DB로 부터 Optional<userDto> 반환
     *
     * @param username
     * @return
     */
    protected abstract Optional<T> getUserByUsernamePasswordFromDb(String username);

    /**
     * AccessToken에 넣을 Payload 반환
     *
     * @param user
     * @return
     */
    protected abstract Map<String, Object> getAccessTokenPayload(T user);


    /**
     * Payload로 Authentication 생성
     *
     * @param payload
     * @return
     */
    protected abstract Authentication getAuthenticationFromPayload(Map<String, Object> payload);

    /**
     * RefreshToken에 넣을 Payload 반환
     *
     * @param user
     * @return
     */
    protected abstract Map<String, Object> getRefreshTokenPayload(T user);

    /**
     * UserDto의 password getter
     *
     * @param user
     * @return
     */
    protected abstract String getCredentials(T user);


    /**
     * 저장소에 RefreshToken과 username 등록하는 메소드
     *
     * @param username
     * @param tokenResponse
     */
    protected abstract void saveAccessRefreshToken(String username, TokenResponse tokenResponse);

    /**
     * 저장소에 AccessToken과 username 등록하는 메소드
     *
     * @param username
     * @param tokenResponse
     */
    protected abstract void saveAccessToken(String username, TokenResponse tokenResponse);

    @Override
    public TokenResponse login(String username, String password) {
        //아이디 비밀번호를 가지고 유저정보 가져오기

        Optional<T> userOptional = this.getUserByUsernamePasswordFromDb(username);
        if (userOptional.isEmpty()) {
            throw new AccountNotFoundException("user not found ", null);
        }
        T user = userOptional.get();

        String dbPassword = this.getCredentials(user);
        if (!this.passwordEncoder.matches(password, dbPassword)) {
            throw new BadCredentialsException("not match RawPassword and DBPassword");
        }

        //access refresh token 생성
        Map<String, Object> accessPayload = this.getAccessTokenPayload(user);
        Map<String, Object> refreshPayload = this.getRefreshTokenPayload(user);
        TokenDto accessToken = this.jwtUtils.generateToken(accessPayload, JwtUtils.TokenType.ACCESS);
        TokenDto refreshToken = this.jwtUtils.generateToken(refreshPayload, JwtUtils.TokenType.REFRESH);

        //토큰 반환
        TokenResponse tokenResponse = new TokenResponse();
        tokenResponse.setAccess(accessToken);
        tokenResponse.setRefresh(refreshToken);

        saveAccessRefreshToken(username, tokenResponse);

        return tokenResponse;
    }

    @Override
    public TokenResponse loginRefreshToken(String username, Map<String, Object> payload, String refreshToken) {
        try {
            JwtUtils.TokenValidDto tokenValidDto = this.jwtUtils.validateToken(refreshToken, JwtUtils.TokenType.REFRESH);
            if (tokenValidDto.isExpired()) {
                throw new AccountExpiredException("expired token, refresh your access token ");
            }
            //db확인
            if (!this.checkRefreshTokenFromDb(username, refreshToken)) {
                throw new InvalidTokenException("Invalid refresh Token ", null);
            }

            //payload
            //access token 발급
            TokenDto tokenDto = this.jwtUtils.generateToken(payload, JwtUtils.TokenType.ACCESS);
            TokenResponse tokenResponse = new TokenResponse();
            tokenResponse.setAccess(tokenDto);
            saveAccessToken(username, tokenResponse);
            return tokenResponse;
        } catch (IllegalArgumentException e) {
            throw new InvalidTokenException("invalid token", e);
        }
    }
}
