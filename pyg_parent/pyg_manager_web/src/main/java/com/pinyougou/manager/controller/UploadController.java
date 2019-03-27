package com.pinyougou.manager.controller;

import entity.Result;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import util.FastDFSClient;

@RestController
public class UploadController {

    private String server_url = "http://192.168.25.133/";

    @RequestMapping("/upload")
    public Result upload(MultipartFile file1){
        try {
            //获取文件后缀
            String filename = file1.getOriginalFilename(); //文件全名
            String substring = filename.substring(filename.lastIndexOf(".")+1); //截取文件扩展名

            //通过工具类加载配置文件
            FastDFSClient client = new FastDFSClient("classpath:config/fdfs_client.conf");
            //通过客户端完成文件上传
            String fildId = client.uploadFile(file1.getBytes(), substring);

            return new Result(true, server_url + fildId);

        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "图片上传失败");
        }
    }
}
