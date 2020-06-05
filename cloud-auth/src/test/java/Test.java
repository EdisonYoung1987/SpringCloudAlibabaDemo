import com.edison.springCloudAlibabaDemo.feignClient.TokenFeignClient;
import feign.Contract;
import feign.Feign;
import feign.auth.BasicAuthRequestInterceptor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class Test {
    private TokenFeignClient tokenFeignClient;

    public static void main(String[] args) {
        System.out.println(new BCryptPasswordEncoder().encode("123456"));

//        String str="clientId=client2, clientSecret=$2a$10, scope=[app], resourceIds=[], authorizedGrantTypes=[authorization_code, password, refresh_token, client_credentials], registeredRedirectUris=null, authorities=[], accessTokenValiditySeconds=432000, refreshTokenValiditySeconds=432000, additionalInformation={}";
//        JSONObject jsonObject= JSON.parseObject(str);


        Test test=new Test();
        test.run();//测试feignClient
    }
    public void run(){
        tokenFeignClient=Feign.builder()
//                .client(client).encoder(encoder).decoder(decoder)
                .contract(new Contract.Default())
                .requestInterceptor(new BasicAuthRequestInterceptor("client2","123456"))
                .target(TokenFeignClient.class,"http://127.0.0.1:9002");
        String res=tokenFeignClient.getToken("password","edison","123456");
        System.out.println(res);
    }
}
