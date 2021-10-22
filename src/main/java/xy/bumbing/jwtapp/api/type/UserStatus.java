package xy.bumbing.jwtapp.api.type;

import lombok.Getter;

import java.util.Arrays;

/**
 * <pre>
 * kr.co.everex.mora.api.type
 * ㄴ UserStatus.java
 * </pre>
 *
 * 회원 상태값 Enum
 *
 * @date : 2021/10/15 11:58 오전
 * @author : Beaver
 * @version : 1.0.0
 **/
@Getter
public enum UserStatus {

    ACTIVATION("Y"), DEACTIVATION("N");

    private final String code;

    UserStatus(String code) {
        this.code = code;
    }

    public static UserStatus of(String code) {
        return Arrays.stream(UserStatus.values()).filter(s -> s.getCode().equals(code)).findFirst().orElseThrow((() -> new IllegalArgumentException("유효하지 않는 파라메터 " + code)));
    }


}
