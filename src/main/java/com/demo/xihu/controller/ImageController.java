package com.demo.xihu.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.demo.xihu.config.IPConfig;
import com.demo.xihu.dto.ImageDTO;
import com.demo.xihu.entity.Image;
import com.demo.xihu.result.Result;
import com.demo.xihu.service.ImageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/images")
@Slf4j
public class ImageController {

    @Autowired
    private ImageService imageService;

    @PostMapping("/save")
    public Result imageUpload(MultipartFile file) throws IOException {
        if(file==null||file.isEmpty()){
            return Result.error("未知的错误");
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
        // 返回保存文件的路径字符串url
        String imageUrl = IPConfig.MY_IP+savePath.toString();
        //保存数据库
        Image image = new Image();
        image.setImageUrl(imageUrl);
        imageService.saveImage(image);

        return Result.success("上传成功",imageUrl);
    }

    /**
     * 传入imageId
     * @param image
     * @return
     */
    @PostMapping("/delete")
    public Result imageRemove(@RequestBody Image image){
        Long imageId = image.getId();
        image = imageService.getOne(new QueryWrapper<Image>().eq("id", imageId));
        //删除服务器上的的文件
        String imageUrl = image.getImageUrl(); // 假设imageUrl包含文件路径

        // 获取JAR包所在目录的路径
        String currentDir = System.getProperty("user.dir");
        // 构建上传目录的路径
        String uploadDirPath = currentDir + File.separator;
        // 获取保存文件的相对路径部分
        String relativePath = imageUrl.substring(IPConfig.MY_IP.length());
        // 构建完整的保存文件路径
        Path savePath = Paths.get(uploadDirPath, relativePath);
        String savedFilePath = savePath.toString();
        // 删除保存的文件
        try {
            Files.delete(savePath);
            log.info("文件已成功删除,路径：{}",savedFilePath);
        } catch (IOException e) {
            log.error("删除文件时出现异常: {}", e.getMessage());
            // 处理异常
        }
        //删除数据库的数据
        imageService.removeById(imageId);

        return Result.success("删除成功");
    }


    @PostMapping("/list")
    public Result imageRemove(@RequestBody ImageDTO imageDTO){
        log.info("传入参数:{}",imageDTO);
        QueryWrapper<Image> imageQueryWrapper = new QueryWrapper<Image>();
        if(imageDTO.getImageType()!=null&&"".equals(imageDTO.getImageType())){
            imageQueryWrapper.eq("image_type",imageDTO.getImageType());
        }
        Page<Image> page = new Page<>(1, imageDTO.getNum()); // 第一页，每页数量为 num
        Page<Image> resultPage = imageService.page(page, imageQueryWrapper);
        List<Image> imageList = resultPage.getRecords();
        return Result.success("查询成功",imageList);
    }
}
