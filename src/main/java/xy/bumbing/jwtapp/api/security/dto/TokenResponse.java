package xy.bumbing.jwtapp.api.security.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TokenResponse {
    TokenDto access;
    TokenDto refresh;
}
