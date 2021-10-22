package xy.bumbing.jwtapp.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xy.bumbing.jwtapp.api.entity.MemberEntity;
import xy.bumbing.jwtapp.api.repository.MemberRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void singUp(MemberEntity user) {

        //해당 메서드 동시 실행시 유저 중복될수 있기 때문에 Email UniqueKey 필수
        Optional<MemberEntity> oldUserOptional = memberRepository.findByEmail(user.getEmail());
        if (oldUserOptional.isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 이메일");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        memberRepository.save(user);

    }
}