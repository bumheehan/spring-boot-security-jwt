package xy.bumbing.jwtapp.api;

import com.ulisesbocchio.jasyptspringboot.encryptor.SimpleGCMConfig;
import com.ulisesbocchio.jasyptspringboot.encryptor.SimpleGCMStringEncryptor;
import org.jasypt.encryption.StringEncryptor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import java.util.Base64;

/**
 * <pre>
 * kr.co.everex.mora.api
 * ㄴ PasswordEncoding.java
 * </pre>
 * <p>
 * Jasypt 테스트
 * createSecret을 사용하여 16자리 문자 base64로 변환하고 resources/secret-jasypt.b64입력
 * encrypt를 사용하여 특정 값 암호화하고 프로퍼티어 ENC(암호화된 코드) 형식으로 추가
 * decrypt를 사용하여 암호화된 값이 정상적으로 디코딩 되는지 확인
 * main함수에서 스프링 올리지 않고 createSecret과 encrypt 한번에 가능
 *
 * @author : Beaver
 * @version : 1.0.0
 * @date : 2021/10/15 10:41 오전
 **/
@SpringBootTest
public class PasswordEncoding {

    @Autowired
    StringEncryptor stringEncryptor;

    @Autowired
    ApplicationContext applicationContext;

    public static void main(String[] args) {
        String secretKey = "everexeverexever";
        String encryptValue = "thisisasecretkeythisisasecretkeythisisasecretkey";
        byte[] base64 = Base64.getEncoder().encode(secretKey.getBytes());
        String secretBase64 = new String(base64);
        System.out.println(secretBase64);
        SimpleGCMConfig config = new SimpleGCMConfig();
        config.setSecretKey(secretBase64);
        config.setSecretKeyIterations(1000);
        config.setSecretKeyAlgorithm("PBKDF2WithHmacSHA256");
        SimpleGCMStringEncryptor encryptor = new SimpleGCMStringEncryptor(config);
        System.out.println(encryptor.encrypt(encryptValue));
    }

    @Test
    public void createSecret() {
        //16자리 코드 필요 (32byte)
        byte[] base64 = Base64.getEncoder().encode("everexeverexever".getBytes());
        System.out.println(new String(base64));
    }

    @Test
    public void encrypt() {
        String test = stringEncryptor.encrypt("test");
        System.out.println(test);


    }

    @Test
    public void decrypt() {
        String property = applicationContext.getEnvironment().getProperty("jasypt.test");
        System.out.println(property);
    }

}
