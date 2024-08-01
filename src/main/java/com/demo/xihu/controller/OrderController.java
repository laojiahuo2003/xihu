package com.demo.xihu.controller;


import com.demo.xihu.dto.CreateOrderDTO;
import com.demo.xihu.result.Result;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("dev-api/order")
@Slf4j
@Tag(name = "订单相关接口", description = "这是描述")
public class OrderController {

    /**
     * 创建订单
     * @param createOrderDto
     * @return
     */
    @PostMapping("/order/create")
    public Result createOrder(@RequestBody CreateOrderDTO createOrderDto) {

        return Result.success("创建订单成功");
    }

}
