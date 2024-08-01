package com.demo.xihu.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.demo.xihu.entity.Guest;
import com.demo.xihu.entity.Image;
import com.demo.xihu.mapper.GuestMapper;
import com.demo.xihu.mapper.ImageMapper;
import com.demo.xihu.service.GuestService;
import com.demo.xihu.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ImageServiceImpl  extends ServiceImpl<ImageMapper, Image> implements ImageService {
    @Autowired
    private ImageMapper imageMapper;

    @Override
    public void saveImage(Image image) {
        imageMapper.insert(image);
    }
}
