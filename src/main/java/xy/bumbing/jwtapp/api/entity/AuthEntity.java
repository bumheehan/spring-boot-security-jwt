package xy.bumbing.jwtapp.api.entity;

import lombok.*;
import xy.bumbing.jwtapp.api.type.Role;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "auth")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AuthEntity extends BaseEntity {


    @Column(length = 15, nullable = false)
    @Enumerated(EnumType.STRING)
    private Role authority;

    @JoinColumn(name = "member_id")
    @OneToOne(fetch = FetchType.LAZY)
    private MemberEntity member;

    @Builder
    public AuthEntity(Role authority) {
        this.authority = authority;
    }
}
