package com.demo.xihu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.demo.xihu.entity.Image;

public interface ImageService extends IService<Image> {
    void saveImage(Image image);
}
