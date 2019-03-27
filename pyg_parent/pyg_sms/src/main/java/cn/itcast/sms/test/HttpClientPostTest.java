package cn.itcast.sms.test;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;

public class HttpClientPostTest {

    public static void main(String[] args) throws IOException {
        //创建CloseableHttpClient客户端，可以理解成打开浏览器
        CloseableHttpClient client = HttpClients.createDefault();

        //https://www.oschina.net/search?scope=project&q=php%E6%98%AF%E6%9C%80%E5%A5%BD%E7%9A%84%E8%AF%AD%E8%A8%80
        //准备get请求
        HttpPost post = new HttpPost("https://www.oschina.net/");

        //封装NameValuePair
        ArrayList<NameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("scope", "project"));
        pairs.add(new BasicNameValuePair("q", "php"));

        //通过UrlEncodedFormEntity封装entity
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(pairs,"UTF-8");
        //设置post请求的内容
        post.setEntity(entity);

        //设置头信息
        post.addHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/68.0.3440.106 Safari/537.36");

        //发送请求,并获取返回值
        CloseableHttpResponse response = client.execute(post);

        System.out.println("返回的状态码"+response.getStatusLine().getStatusCode());

        //通过EntityUtils将返回的entity转成可读的String
        String string = EntityUtils.toString(response.getEntity(), "UTF-8");

        System.out.println(string);
    }
}
