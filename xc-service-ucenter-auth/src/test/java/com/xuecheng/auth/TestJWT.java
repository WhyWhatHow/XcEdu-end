package com.xuecheng.auth;

import com.alibaba.fastjson.JSON;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaSigner;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;
import org.springframework.security.rsa.crypto.KeyStoreKeyFactory;

import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * @program: XcEduCode
 * @description:
 * @author: WhyWhatHow
 * @create: 2020-04-22 10:19
 **/
public class TestJWT {
    String keyStore = "www.keystore";
    String keyStorePwd = "xuechengkeystore";
    String keyPwd = "xuecheng";
    String alias = "wwwkey";
    private String token;

    @Test
    public void testBase64(){
        String input = "WGNXZWJBcHA6WGNXZWJBcHA=";
        byte[] decode = Base64.getDecoder().decode(input.getBytes());
        String out = new String(decode);
        System.out.println(out);
    }

    @Test
    /**
     * 测试生成JWT令牌 JSON web Token
     * 1. 生成秘钥对
     * 2. 获取私钥
     * 3. 添加额外信息
     * 4 .生成JWT令牌
     */
    public void testGenerateJWT() {
        ClassPathResource resource = new ClassPathResource(keyStore);
        boolean file = resource.isFile();
        // 1. generate public|private key pair
        KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(resource, keyStorePwd.toCharArray());
        KeyPair keyPair = keyStoreKeyFactory.getKeyPair(alias);
//        电脑问题，生成密钥对时 未注册-keypass
//        KeyPair keyPair1 = keyStoreKeyFactory.getKeyPair(alias,keyPwd.toCharArray());

        // 2  get private key
        RSAPrivateKey aPrivate = (RSAPrivateKey) keyPair.getPrivate();
        // 3 add another info

        Map<String, Object> map = new HashMap<>();
        map.put("name", "whywhathow");
        map.put("id", "123");

        String s = JSON.toJSONString(map);
        // 4 generate JWT
        Jwt jwt = JwtHelper.encode(s, new RsaSigner(aPrivate));
        token = jwt.getEncoded();
        System.out.println(token);
//        token
//    eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJuYW1lIjoid2h5d2hhdGhvdyIsImlkIjoiMTIzIn0.GpO640-v0t5KMoHov5y-P9ldVI2YfYRTRs4DEQQq7dTKp1J7mD0DQjqkP2wldzh9UFFBrTquYJhvx7WF_qgAlQwH3uXDz-YQD5AGeJsqjwG2ZFSfKb6DnCQB2ZkytBNYweKzrg9WAqz12pcbTWxYtNFhpwqqA6RLpDWOZ156HYp_4O9qJkEt3FZUUksgsZd1A8T20WT-FfS5maWJjTYo5T7H5wmC40oO-0QsWW1Sxqa0-EvzJvLc-yTLUqn7axIjqJgrGQ9bjIF-KD4hHa3KASeHDAAVNMQa2TMt2U9BEg1Ie3XRoOIsqHuPDl0UAbw-hYparytZfxp4cieWPk-wuQ
    }


    /**
     * verify token
     */
    @Test
    public void testVerify() {
        String token ="eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJuYW1lIjoid2h5d2hhdGhvdyIsImlkIjoiMTIzIn0.fGhUPkMQFmkThJTGbAWVCJTb112ZIL6PMhshUHeuK0-Jy0XfuNa06w3zBvsDVEpyb0X63XAclzyQvdhCmN1kZodX6dL4PFxfR4WUCvnQXJbgOuPk3d2KtKI1E49MlWxUAKV_zaJb99mqUK3o-sfhzL2ysysu7QgprYHUdw4IUtKZ6Ovi8kEi6tZW-Rs44_lBJtjvXkEVWyLdl9zSuqSp_3gKqad0rOMPrmlkw7LIB0ZNu-wkD2CTUtBouuM6SHNySG_TrjQE3bSd7rl0Xp5IRI30jhkSfaTxXOFdAaIdN1iLhl7dnUstRsr7nLakd2r34Xrwtopivtb39vRhdVO-jw";
        System.out.println(token);
        String publicKey ="-----BEGIN PUBLIC KEY-----MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAg/Fc1RtOwZgYU1r427r9izv+LF8Mnji3HwuRCwRDYQcXm27fBHdKsFMj0uKuW6UaHJj0HEP3FiqsNg/lwor4FZ3hSyDJ9+e1vDZyvfk4+gjRVsu9PsHa20l691ptomrKpL5LmeEKG/zc0Bcy8J8pF+xVWduoSiLh34PGeivKQwBW5nS+/5shaJX5EYypdz4cm5wydmv7F9i/uMQyFUZgrynp9Xt76fvInFl1zU5bgMNJJkmy4gpn80BvpBGhTq8rhuV7WpBe9oksYt2LSt7bpuxpUN6MVCGpxOtMWK9cblLjh2yJ0BIy0q7ROG5GUW0WwvNx0rUzqz8mNYADui+PxwIDAQAB-----END PUBLIC KEY-----";
        Jwt jwt = JwtHelper.decodeAndVerify(token, new RsaVerifier(publicKey));
        String encoded = jwt.getEncoded();
        System.out.println(encoded);
    }

    @Test
    public void testPassWordEncoder(){
        String pwd ="111111";
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        for (int i = 0; i <10 ; i++) {
            String encode = encoder.encode(pwd);
            System.out.println(encode);
            System.out.println("====================");
        }
    }
}
