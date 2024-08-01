package com.demo.xihu.dto;

import com.demo.xihu.entity.Image;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImageDTO extends Image {

    private Integer num;
}
