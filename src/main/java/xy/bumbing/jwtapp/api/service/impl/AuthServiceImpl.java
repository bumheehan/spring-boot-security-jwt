package xy.bumbing.jwtapp.api.service.impl;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import xy.bumbing.jwtapp.api.entity.TokenEntity;
import xy.bumbing.jwtapp.api.repository.MemberRepository;
import xy.bumbing.jwtapp.api.repository.TokenRepository;
import xy.bumbing.jwtapp.api.repository.dto.MemberDto;
import xy.bumbing.jwtapp.api.security.dto.TokenResponse;
import xy.bumbing.jwtapp.api.security.service.AbstractAuthService;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class AuthServiceImpl extends AbstractAuthService<MemberDto> {

    private final TokenRepository tokenRepository;
    private final MemberRepository memberRepository;
    private static final String ROLE_PARAMETER = "email";
    private static final String USER_PARAMETER = "user";

    public AuthServiceImpl(TokenRepository tokenRepository, MemberRepository memberRepository) {
        this.tokenRepository = tokenRepository;
        this.memberRepository = memberRepository;
    }

    @Override
    protected boolean checkRefreshTokenFromDb(String username, String refreshToken) {
        return tokenRepository.findByRefreshToken(refreshToken).isPresent();
    }

    @Override
    protected Optional<MemberDto> getUserByUsernamePasswordFromDb(String username) {
        return memberRepository.findByEmail(username).map(MemberDto::new);
    }

    @Override
    protected Map<String, Object> getAccessTokenPayload(MemberDto member) {
        Map<String, Object> payload = new HashMap<>();
        payload.put(USER_PARAMETER, member.getEmail());
        payload.put(ROLE_PARAMETER, member.getAuth().getAuthority().name());
        return payload;
    }

    @Override
    protected Authentication getAuthenticationFromPayload(Map<String, Object> payload) {
        String user = (String) payload.get(USER_PARAMETER);
        List<String> auth = (List<String>) payload.get(ROLE_PARAMETER);

        Collection<GrantedAuthority> authorities = auth.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user, null, authorities);

        authenticationToken.setDetails(payload);
        return authenticationToken;
    }

    @Override
    protected Map<String, Object> getRefreshTokenPayload(MemberDto member) {
        Map<String, Object> payload = new HashMap<>();
        payload.put(USER_PARAMETER, member.getEmail());
        return payload;
    }

    @Override
    protected String getCredentials(MemberDto user) {
        return user.getPassword();
    }

    @Override
    @Transactional
    protected void saveAccessRefreshToken(String username, TokenResponse tokenResponse) {
        TokenEntity token = new TokenEntity();
        token.setRefreshToken(tokenResponse.getRefresh().getToken());
        ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpServletRequest servletRequest = sra.getRequest();
        token.setConnectIp(servletRequest.getRemoteAddr());
        tokenRepository.save(token);
    }

    @Override
    @Transactional
    protected void saveAccessToken(String username, TokenResponse tokenResponse) {

    }
}
