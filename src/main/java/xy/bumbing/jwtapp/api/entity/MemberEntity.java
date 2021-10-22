package xy.bumbing.jwtapp.api.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "member")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberEntity extends BaseEntity {

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "phone", nullable = false)
    private String phone;

    @OneToOne(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    TokenEntity token;

    @OneToOne(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    AuthEntity auth;

    @Builder
    public MemberEntity(String name, String email, String password, String phone, TokenEntity token, AuthEntity auth) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.token = token;
        this.auth = auth;
    }
}