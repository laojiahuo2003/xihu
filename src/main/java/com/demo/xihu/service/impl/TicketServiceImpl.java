package com.demo.xihu.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.demo.xihu.entity.Ticket;
import com.demo.xihu.entity.TicketOrder;
import com.demo.xihu.mapper.TicketMapper;
import com.demo.xihu.mapper.TicketOrderMapper;
import com.demo.xihu.result.Result;
import com.demo.xihu.service.TicketOrderService;
import com.demo.xihu.service.TicketService;
import org.springframework.stereotype.Service;

@Service
public class TicketServiceImpl extends ServiceImpl<TicketMapper, Ticket> implements TicketService {

}
