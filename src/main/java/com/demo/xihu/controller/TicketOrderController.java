package com.demo.xihu.controller;

import com.demo.xihu.result.Result;
import com.demo.xihu.service.TicketOrderService;
import com.demo.xihu.service.TicketService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ticket-order")
@Tag(name = "抢购票订单接口")
public class TicketOrderController {
    @Autowired
    private TicketOrderService ticketOrderService;
    @PostMapping("/buy/{id}")
    public Result saveTicketOrder(@PathVariable("id") Long ticketId){
        return ticketOrderService.saveTicketOrder(ticketId);
    }

}
