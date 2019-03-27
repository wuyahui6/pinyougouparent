package cn.itcast.sms.test;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class HttpClientGetTest {

    public static void main(String[] args) throws IOException {
        //创建CloseableHttpClient客户端，可以理解成打开浏览器
        CloseableHttpClient client = HttpClients.createDefault();

        //准备get请求
        HttpGet get = new HttpGet("http://www.baidu.com/s?wd=php");

        //发送请求,并获取返回值
        CloseableHttpResponse response = client.execute(get);

        System.out.println("返回的状态码"+response.getStatusLine().getStatusCode());

        //通过EntityUtils将返回的entity转成可读的String
        String string = EntityUtils.toString(response.getEntity(), "UTF-8");

        System.out.println(string);
    }
}
