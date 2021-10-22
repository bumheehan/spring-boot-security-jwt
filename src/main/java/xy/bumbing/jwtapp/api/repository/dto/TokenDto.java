package xy.bumbing.jwtapp.api.repository.dto;

import lombok.Data;
import xy.bumbing.jwtapp.api.entity.TokenEntity;

import java.time.LocalDateTime;

@Data
public class TokenDto {

    private final String refreshToken;
    private final String connectIp;
    private final Long id;
    private final LocalDateTime createdDate;
    private final LocalDateTime modifiedDate;

    TokenDto(TokenEntity token) {
        refreshToken = token.getRefreshToken();
        connectIp = token.getConnectIp();
        id = token.getId();
        createdDate = token.getCreatedDate();
        modifiedDate = token.getModifiedDate();
    }
}
