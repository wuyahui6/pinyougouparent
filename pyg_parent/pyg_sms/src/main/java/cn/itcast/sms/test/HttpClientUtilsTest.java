package cn.itcast.sms.test;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import util.HttpClientUtil;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

public class HttpClientUtilsTest {

    public static void main(String[] args) throws IOException, ParseException {
        HttpClientUtil util1 = new HttpClientUtil("http://localhost:9101/login");

        //给util设置post请求参数
        Map map = new HashMap();
        map.put("username", "admin");
        map.put("password", "123456");
        util1.setParameter(map);
        //发送post请求
        util1.post();


        //准备HttpClientUtil
        HttpClientUtil util = new HttpClientUtil("http://localhost:9101/specification/findAll.do");

        //如果有参数记得在发送前设置参数
        /*util.setParameter();*/
        //发送get请求
        util.get();

        //获取cotent内容
        String content = util.getContent();
        System.out.println(content);
    }
}
