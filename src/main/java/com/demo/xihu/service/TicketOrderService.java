package com.demo.xihu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.demo.xihu.entity.TicketOrder;
import com.demo.xihu.result.Result;

public interface TicketOrderService extends IService<TicketOrder> {
    Result saveTicketOrder(Long ticketId);
}
