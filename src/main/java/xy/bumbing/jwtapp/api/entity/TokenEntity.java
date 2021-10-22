package xy.bumbing.jwtapp.api.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "token")
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class TokenEntity extends BaseEntity {

    @Column(length = 128, nullable = false)
    private String refreshToken;

    @Column(length = 45, nullable = false)
    private String connectIp;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    MemberEntity member;

}
