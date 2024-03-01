package com.demo.xihu.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@RestController
@RequestMapping("/dev-api")
@Slf4j
public class UploadController {

    public static final String MY_IP = "http://8.130.55.70:8080";
    @PostMapping("/upload")
    public String upload(MultipartFile file) throws IOException {
        if(file==null||file.isEmpty()){
            log.info("目录路径:默认");
            return MY_IP+"/image/1.jpg";
        }
        // 获取JAR包所在目录的路径
        String currentDir = System.getProperty("user.dir");
        log.info("目录路径:{}",currentDir);
        // 构建上传目录的路径
        String uploadDirPath = currentDir + File.separator + "image";

        // 创建File对象指向上传目录
        File uploadDir = new File(uploadDirPath);

        // 如果目录不存在，则创建它
        if (!uploadDir.exists() && !uploadDir.mkdirs()) {
            throw new IOException("无法创建上传目录: " + uploadDirPath);
        }

        // 获取原始文件名并提取扩展名
        String originalFilename = file.getOriginalFilename();
        String ext = originalFilename != null ? originalFilename.substring(originalFilename.lastIndexOf('.')) : "";

        // 生成UUID作为新文件名
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String fileName = uuid + ext;

        // 创建保存文件的路径
        Path savePath = Paths.get(uploadDirPath, fileName);

        // 将上传的文件保存到目标目录
        Files.copy(file.getInputStream(), savePath);
        log.info("保存目录:{}",savePath);
        // 返回保存文件的路径字符串
        return MY_IP+savePath.toString();
    }
}
