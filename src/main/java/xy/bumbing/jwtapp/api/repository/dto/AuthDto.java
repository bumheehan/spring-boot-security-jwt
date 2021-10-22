package xy.bumbing.jwtapp.api.repository.dto;

import lombok.Data;
import xy.bumbing.jwtapp.api.entity.AuthEntity;
import xy.bumbing.jwtapp.api.type.Role;

import java.time.LocalDateTime;

@Data
public class AuthDto {
    private final Long id;
    private final LocalDateTime createdDate;
    private final LocalDateTime modifiedDate;
    private final Role authority;

    AuthDto(AuthEntity auth) {
        authority = auth.getAuthority();
        id = auth.getId();
        createdDate = auth.getCreatedDate();
        modifiedDate = auth.getModifiedDate();
    }

}
