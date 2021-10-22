package xy.bumbing.jwtapp.api.security.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import xy.bumbing.jwtapp.api.security.dto.TokenDto;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Slf4j
public class JwtUtils {
    private final Key accessKey;
    private final Key refreshKey;
    private final Integer accessExpiresIn;
    private final Integer refreshExpireIn;

    public JwtUtils(String accessSecretKey,
                    String refreshSecretKey,
                    Integer accessExpired,
                    Integer refreshExpired) {

        this.accessKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(accessSecretKey));
        this.refreshKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(refreshSecretKey));
        this.accessExpiresIn = accessExpired;
        this.refreshExpireIn = refreshExpired;
    }

    public TokenValidDto validateToken(String token, TokenType tokenType) {
        TokenValidDto tokenValid = new TokenValidDto();
        try {
            tokenValid.setPayload(Jwts.parserBuilder().setSigningKey(TokenType.ACCESS.equals(tokenType) ? this.accessKey : this.refreshKey).build().parseClaimsJws(token).getBody());
        } catch (ExpiredJwtException e) {
            tokenValid.setExpired(true);
            tokenValid.setPayload(e.getClaims());
        } catch (Exception e) {
            throw new IllegalArgumentException("invalid token" + token, e);
        }
        return tokenValid;
    }

    @Data
    public static class TokenValidDto {
        private boolean expired;
        private Map<String, Object> payload;
    }


    public TokenDto generateToken(Map<String, Object> payload, TokenType tokenType) {
        try {
            Claims claims = Jwts.claims(payload);
            Instant now = Instant.now();
            Date issueAt = Date.from(now);

            TokenDto tokenDto = new TokenDto();
            if (TokenType.ACCESS.equals(tokenType)) {
                Date expireAt = Date.from(now.plusSeconds(this.accessExpiresIn));
                tokenDto.setToken(Jwts.builder().setClaims(claims).setIssuedAt(issueAt).setExpiration(expireAt).signWith(this.accessKey, SignatureAlgorithm.HS256).compact());
                tokenDto.setExpiresIn(Long.toString(this.accessExpiresIn));
            } else {
                Date expireAt = Date.from(now.plusSeconds(this.refreshExpireIn));
                tokenDto.setToken(Jwts.builder().setClaims(claims).setIssuedAt(issueAt).setExpiration(expireAt).signWith(this.refreshKey, SignatureAlgorithm.HS256).compact());
                tokenDto.setExpiresIn(Long.toString(this.refreshExpireIn));
            }
            return tokenDto;
        } catch (Exception e) {
            throw new IllegalStateException("failed to generate jwt");
        }
    }

    public enum TokenType {
        ACCESS, REFRESH
    }


}