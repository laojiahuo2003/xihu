package com.demo.xihu.controller;

import com.demo.xihu.entity.Ticket;
import com.demo.xihu.result.Result;
import com.demo.xihu.service.TicketService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ticket")
@Tag(name = "票务接口")
public class TicketController {
    @Autowired
    private TicketService ticketService;

    @PostMapping("/add")
    public Result addTicket(@RequestBody Ticket ticket){
        ticketService.save(ticket);
        return Result.success("添加成功");
    }

}
