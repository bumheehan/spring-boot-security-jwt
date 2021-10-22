package xy.bumbing.jwtapp.api.controller;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xy.bumbing.jwtapp.api.entity.AuthEntity;
import xy.bumbing.jwtapp.api.entity.MemberEntity;
import xy.bumbing.jwtapp.api.service.MemberService;
import xy.bumbing.jwtapp.api.type.Role;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Slf4j
@RestController
@RequestMapping("/api/user")
public class MemberController {

    @Autowired
    private MemberService memberService;

    @PutMapping("/signup")
    public void signup(@RequestBody @Valid SignUpRequest signUpRequest) {
        AuthEntity auth = AuthEntity.builder().authority(Role.valueOf(signUpRequest.getRole())).build();
        MemberEntity member = MemberEntity.builder()
                .name(signUpRequest.getName())
                .password(signUpRequest.getPassword())
                .email(signUpRequest.getEmail())
                .phone(signUpRequest.getPhone())
                .auth(auth).build();
        auth.setMember(member);

        memberService.singUp(member);
    }

    @Data
    @ApiModel
    public static class SignUpRequest {

        @NotBlank(message = "아이디 유효성 검사 실패")
        @Pattern(regexp = "([a-zA-Z]{3,45})|([가-힣]{3,45})", message = "아이디 유효성 검사 실패")
        @ApiModelProperty(example = "한한한")
        private String name;

        @NotBlank(message = "이메일 유효성 검사 실패")
        @Email(message = "이메일 유효성 검사 실패")
        @ApiModelProperty(example = "hbh@bumbing.xyz")
        private String email;

        @NotBlank(message = "비밀번호 유효성 검사 실패")
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*[!\"#$%&'()*+,\\-./:;<=>?@\\[\\]^_`{|}~\\\\])(?=.*[0-9])[0-9A-Za-z!\"#$%&'()*+,\\-./:;<=>?@\\[\\]^_`{|}~\\\\]{8,16}$", message = "비밀번호 유효성 검사 실패")
        @ApiModelProperty(example = "testTest1!@#")
        private String password;

        @NotBlank(message = "휴대폰번호 유효성 검사 실패")
        @Pattern(regexp = "^\\d{3}-\\d{3,4}-\\d{4}$", message = "휴대폰번호 유효성 검사 실패")
        @ApiModelProperty(example = "010-0000-0000")
        private String phone;


        private String role;

    }

    //앞단에서 가로챔
    @PostMapping("/signin")
    public void signIn(@RequestBody @Valid SignInRequest signInRequest) {

    }

    @Data
    @ApiModel
    public static class SignInRequest {
        @NotBlank(message = "이메일 유효성 검사 실패")
        @Email(message = "이메일 유효성 검사 실패")
        @ApiModelProperty(example = "hbh@bumbing.xyz")
        private String email;

        @NotBlank(message = "비밀번호 유효성 검사 실패")
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*[!\"#$%&'()*+,\\-./:;<=>?@\\[\\]^_`{|}~\\\\])(?=.*[0-9])[0-9A-Za-z!\"#$%&'()*+,\\-./:;<=>?@\\[\\]^_`{|}~\\\\]{8,16}$", message = "비밀번호 유효성 검사 실패")
        @ApiModelProperty(example = "testTest1!@#")
        private String password;
    }


    @GetMapping("/test/guest")
    public static String testGuest() {
        return "OK";
    }

    @GetMapping("/test/admin")
    public static String testAdmin() {
        return "OK";
    }

    @GetMapping("/test/user")
    public static String testUser() {
        return "OK";
    }

    @GetMapping("/test/manager")
    public static String testManager() {
        return "OK";
    }

}
