package xy.bumbing.jwtapp.api.security.dto;

import lombok.Data;

@Data
public class TokenDto {
    private String token;
    private String expiresIn;
}