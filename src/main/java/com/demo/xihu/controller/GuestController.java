package com.demo.xihu.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.demo.xihu.dto.GuestListDTO;
import com.demo.xihu.dto.QueryGoodactivitiesDTO;
import com.demo.xihu.entity.Goodactivity;
import com.demo.xihu.result.Result;
import com.demo.xihu.service.GuestService;
import com.demo.xihu.vo.GuestShowVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("dev-api")
@Slf4j
@Tag(name = "嘉宾相关接口", description = "这是描述")
public class GuestController {


    @Autowired
    private GuestService guestService;

    /**
     *嘉宾姓名，大会日期，是否专家查询，查询数量
     *
     * @param guestListDTO
     * @return
     */
    @PostMapping("/guests/list")
    @Operation(summary = "根据条件查询嘉宾")
    public Result queryGuestsAndGoodactivity(@RequestBody GuestListDTO guestListDTO){
        List<GuestShowVO> guestShowVO = guestService.getGuestsList(guestListDTO);
        return Result.success("查询成功",guestShowVO);
    }

    /**
     * 查询所有嘉宾数据和他的参会信息
     * @return
     */
    @GetMapping("/guests/allInfo")
    @Operation(summary = "查询所有的嘉宾信息")
    public Result queryAllGuestsAndGoodactivity(){
        List<GuestShowVO> guestShowVO = guestService.getGuests();
        return Result.success("查询成功",guestShowVO);
    }
}
