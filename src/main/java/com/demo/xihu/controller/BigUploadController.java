package com.demo.xihu.controller;

import com.demo.xihu.result.Result;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
@Tag(name = "大文件上传接口", description = "分片上传")
@RestController
public class BigUploadController {
    // 上传路径
    public static final String UPLOAD_PATH = "C:\\Users\\29443\\Desktop\\upload\\";


    /**
     * 前端多次发起请求，后端不断利用RandomAccessFile在某个偏移量中写入文件数据，并且修改配置文件数组为1，代表已上传
     * @param chunkSize 分片大小
     * @param md5 文件总md5
     * @param totalNumber 总大小
     * @param chunkNumber 当前传输的分片
     * @param file 当前分片文件
     * @return
     */
    @RequestMapping("/uploadBig")
    public Result uploadBig(@RequestParam Long chunkSize,@RequestParam String md5, @RequestParam Integer totalNumber, @RequestParam Long chunkNumber, @RequestParam MultipartFile file) throws IOException {
        //文件存放位置(设置文件目录和文件名)
        String dstFile = String.format("%s\\%s\\%s.%s", UPLOAD_PATH, md5, md5, StringUtils.getFilenameExtension(file.getOriginalFilename()));
        //上传分片信息存放位置
        String confFile = String.format("%s\\%s\\%s.conf", UPLOAD_PATH, md5, md5);
        //第一次创建分片记录文件
        //创建目录
        File dir = new File(dstFile).getParentFile();
        if (!dir.exists()) {
            dir.mkdir();
            //所有分片状态设置为0
            byte[] bytes = new byte[totalNumber];
            Files.write(Path.of(confFile), bytes);
        }
        //随机分片写入文件
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(dstFile, "rw");
             RandomAccessFile randomAccessConfFile = new RandomAccessFile(confFile, "rw");
             InputStream inputStream = file.getInputStream()) {
            //定位到该分片的偏移量
            randomAccessFile.seek(chunkNumber * chunkSize);
            //写入该分片数据
            randomAccessFile.write(inputStream.readAllBytes());
            //定位到当前分片状态位置
            randomAccessConfFile.seek(chunkNumber);
            //设置当前分片上传状态为1
            randomAccessConfFile.write(1);
        }
        return Result.success(Map.of("path", dstFile));
    }

    /**
     * 通过md5码判断文件是否完全上传，并且返回上传情况
     * @param md5
     * @return
     * @throws IOException
     */
    @RequestMapping("/checkFile")
    public Result checkFile(@RequestParam String md5) throws IOException {
        // 获取配置文件的位置
        String confFile = String.format("%s\\%s\\%s.conf", UPLOAD_PATH, md5, md5);
        Path path = Path.of(confFile);
        // 不存在说明没有上传过该md5码文件
        if (!Files.exists(path.getParent())) {
            return Result.error("文件未上传");
        }
        // 判断文件是否上传成功
        StringBuilder stringConf = new StringBuilder();
        // 读取配置文件
        byte[] bytes = Files.readAllBytes(path);
        // 将数字转换成字符串
        for (byte b : bytes) {
            stringConf.append(String.valueOf(b));
        }
        if (stringConf.toString().contains("0")) {
            File file = new File(String.format("%s\\%s\\", UPLOAD_PATH, md5));
            File[] files = file.listFiles();
            String filePath = "";
            for (File f : files) {
                //计算文件MD5是否相等
                if (!f.getName().contains("conf")) {
                    filePath = f.getAbsolutePath();
                    try (InputStream inputStream = new FileInputStream(f)) {
                        String md5pwd = DigestUtils.md5DigestAsHex(inputStream);
                        if (!md5pwd.equalsIgnoreCase(md5)) {
                            return Result.error("文件上传失败");
                        }
                    }
                }
            }
            return Result.success(Map.of("path", filePath));
        } else {
            //文件未上传完成，反回每个分片状态，前端将未上传的分片继续上传
            return Result.error("文件片段缺失",Map.of("chucks", stringConf.toString()));
        }
    }

}
