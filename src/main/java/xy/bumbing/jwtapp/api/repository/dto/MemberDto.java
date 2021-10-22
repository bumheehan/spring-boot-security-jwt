package xy.bumbing.jwtapp.api.repository.dto;

import lombok.Data;
import xy.bumbing.jwtapp.api.entity.MemberEntity;

import java.time.LocalDateTime;

@Data
public class MemberDto {

    private String name;
    private final String email;
    private final String password;
    private final String phone;
    //    private final TokenDto token;
    private final AuthDto auth;
    private final Long id;
    private final LocalDateTime createdDate;
    private final LocalDateTime modifiedDate;

    public MemberDto(MemberEntity memberEntity) {
        name = memberEntity.getName();
        name = memberEntity.getName();
        email = memberEntity.getEmail();
        password = memberEntity.getPassword();
        phone = memberEntity.getPhone();
//        token = new TokenDto(memberEntity.getToken());
        auth = new AuthDto(memberEntity.getAuth());
        id = memberEntity.getId();
        createdDate = memberEntity.getCreatedDate();
        modifiedDate = memberEntity.getModifiedDate();
    }
}
