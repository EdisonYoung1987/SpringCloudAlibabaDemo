import com.edison.springCloudAlibabaDemo.feignClient.TokenFeignClient;
import feign.Contract;
import feign.Feign;
import feign.auth.BasicAuthRequestInterceptor;

public class Test {
    private TokenFeignClient tokenFeignClient;

    public static void main(String[] args) {
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
