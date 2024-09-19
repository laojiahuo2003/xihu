package com.demo.xihu.lock;

import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class SimpleRedisLock implements ILock{
    private static final String KEY_PREFIX="lock:";
    private static final String ID_PREFIX = UUID.randomUUID() + "-";

    private StringRedisTemplate stringRedisTemplate;
    private String name;
    public SimpleRedisLock(String name, StringRedisTemplate stringRedisTemplate) {
        this.name = name;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public boolean tryLock(long timeoutSec) {
        // 需要给每一个线程唯一的value，防止操作时间大于过期时间，删除别人的锁
        String threadId = ID_PREFIX+Thread.currentThread().getId();
        Boolean success = stringRedisTemplate.opsForValue().setIfAbsent(KEY_PREFIX + name, threadId, timeoutSec, TimeUnit.SECONDS);
        return Boolean.TRUE.equals(success);
    }

    /**
     * 会出现问题，因为get和释放非原子性,需要使用Lua脚本保证redis操作的原子性
     */
    @Override
    public void unlock() {
        String threadId = ID_PREFIX+Thread.currentThread().getId();
        // 获取锁中的标示
        String id = stringRedisTemplate.opsForValue().get(KEY_PREFIX + name);
        // 判断标示是否一致
        if(threadId.equals(id)) {
            // 释放锁
            stringRedisTemplate.delete(KEY_PREFIX + name);
        }
    }
}
