package com.demo.xihu.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.demo.xihu.entity.Ticket;
import com.demo.xihu.entity.TicketOrder;
import com.demo.xihu.entity.User;
import com.demo.xihu.lock.SimpleRedisLock;
import com.demo.xihu.mapper.TicketOrderMapper;
import com.demo.xihu.mapper.UserMapper;
import com.demo.xihu.result.Result;
import com.demo.xihu.service.TicketOrderService;
import com.demo.xihu.service.TicketService;
import com.demo.xihu.service.UserService;
import com.demo.xihu.utils.RedisIdWorker;
import com.demo.xihu.utils.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class TicketOrderServiceImpl extends ServiceImpl<TicketOrderMapper, TicketOrder> implements TicketOrderService {
    @Autowired
    private TicketService ticketService;
    @Autowired
    private RedisIdWorker redisIdWorker;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Result saveTicketOrder(Long ticketId) {
        // 获取票的信息
        Ticket ticket = ticketService.getById(ticketId);
        //一人一单逻辑
        Map<String, Object> claims = ThreadLocalUtil.get();
        Integer userId = (Integer) claims.get("id");
        Long count = query().eq("user_id", userId).eq("ticket_id", ticketId).count();
        if(count>0){
            return Result.error("不允许多次购买");
        }
        // 判断库存信息
        if(ticket.getStock()<1){
            return Result.error("库存不足");
        }
        // 扣减库存(利用乐观锁思想解决超卖问题)
        boolean updated = ticketService.update().setSql("stock = stock - 1").eq("id", ticketId).update();
        if(!updated){
            Result.error("库存不足");
        }
        // 创建分布式锁(目的是解决一人一单在分布式系统上出现的问题,每个用户一个锁)
        SimpleRedisLock lock = new SimpleRedisLock("order:"+userId,stringRedisTemplate);
        boolean isLock = lock.tryLock(1200);
        if(!isLock){
            return Result.error("加锁失败,不允许重复购买");
        }
        // 创建订单
        TicketOrder ticketOrder = new TicketOrder();
        long nextId = redisIdWorker.nextId("order");
        ticketOrder.setId(nextId);
        ticketOrder.setUserId(Long.valueOf(userId));
        ticketOrder.setTicketId(ticketId);
        save(ticketOrder);
        // 释放锁
        lock.unlock();
        return Result.success("抢购成功",ticketOrder);
    }
}
