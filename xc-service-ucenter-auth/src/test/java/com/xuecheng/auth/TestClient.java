package com.xuecheng.auth;

import com.alibaba.fastjson.JSON;
import com.xuecheng.framework.client.XcServiceList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Base64;
import java.util.Map;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TestClient {

    @Autowired
    RestTemplate restTemplate;
    @Autowired
    LoadBalancerClient loadBalancerClient;
    String clientId="XcWebApp";
    String clientSecert="XcWebApp";

    /**
     * 测试申请令牌
     * 1.获取url 利用eureka 获取
     * 2. 设置header:
     * basic authorization ,
     * grant_type
     * username
     * password
     * 3.restTemplate 发送消息,获取对象
     */
    String username ="itcast";
    String password = "123";
    @Test
    public void testClient() {
        //1 . get uri
        ServiceInstance choose = loadBalancerClient.choose(XcServiceList.XC_SERVICE_UCENTER_AUTH);
        URI uri = choose.getUri();
        String url = uri+"/auth/oauth/token";
        // 2 set header
        MultiValueMap<String, String> headers =new LinkedMultiValueMap<String, String>();
        String base64 = getHttpBasicAuth(clientId,clientSecert);
        headers.add("Authorization", base64);

//         Base64.getEncoder().encode();
//        // 3 set body
        MultiValueMap<String, String> body =new LinkedMultiValueMap<String, String>();
        body.set("grant_type","password");
        body.set("username",username);
        body.set("password", password);

        HttpEntity entity = new HttpEntity(body, headers);
        //// TODO: 2020/4/22   处理401 ,403
        //指定 restTemplate当遇到400或401响应时候也不要抛出异常，也要正常返回值
//        restTemplate.setErrorHandler(new DefaultResponseErrorHandler(){
//            @Override
//            public void handleError(ClientHttpResponse response) throws IOException {
////当响应的值为400或401时候也要正常响应，不要抛出异常
//                if(response.getRawStatusCode()!=400 && response.getRawStatusCode()!=401){
//                    super.handleError(response);
//                }
//            }
//        });
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);

        Map map = response.getBody();
        String s = JSON.toJSONString(map);
        System.out.println(s);
    }

    // 获取base 64
    private String getHttpBasicAuth(String clientId, String clientSecert) {
        String s = clientId + ":" + clientSecert;
        byte[] encode = Base64.getEncoder().encode(s.getBytes());
        String out = new String(encode);
        return "Basic " + out;
    }

}
