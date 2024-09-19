package com.demo.xihu.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.demo.xihu.bo.RedisActivityData;
import com.demo.xihu.constant.CacheConstant;
import com.demo.xihu.dto.DateActivitiesVO;
import com.demo.xihu.dto.QueryActivitiesDTO;
import com.demo.xihu.entity.Activity;
import com.demo.xihu.result.Result;
import com.demo.xihu.service.ActivityService;
import com.demo.xihu.service.RedisService;
import com.demo.xihu.service.RegistrationService;
import com.demo.xihu.utils.JwtUtil;
import com.demo.xihu.utils.ThreadLocalUtil;
import com.demo.xihu.vo.ActivityListVO;
import com.github.benmanes.caffeine.cache.Cache;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/activities")
@Slf4j
@Tag(name = "活动(大会)相关接口", description = "这是描述")
public class ActivityController {

    private static final ExecutorService CACHE_REBUILD_EXECUTOR = Executors.newFixedThreadPool(10);
//    private final ReentrantLock lock = new ReentrantLock(); // 局部锁

    @Autowired
    private RegistrationService registrationService;
    @Autowired
    private ActivityService activityService;
    @Autowired
    private Cache<String, Object> cache;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private boolean tryLock(String key) {
        // 设置10秒钟过期时间,设置成功则value变成1，返回true
        Boolean flag = stringRedisTemplate.opsForValue().setIfAbsent(key, "1", 10, TimeUnit.SECONDS);
        return BooleanUtils.isTrue(flag);
    }

    private void unlock(String key) {
        stringRedisTemplate.delete(key);
    }
    /**
     * 选择使用两级缓存,Redis+Caffeine提高查询速度
     * @return
     */
    @GetMapping("/Info")
    @Operation(summary = "搜索订阅的活动")
    public Result getActivityByToken() {
        Map<String, Object> claims = ThreadLocalUtil.get();
        Integer userid = (Integer) claims.get("id");
        log.info("ActivityController getActivityByToken 搜索订阅的活动 用户id{}", userid);

        String key = CacheConstant.ACTIVITY_REGISTER + userid;

        @SuppressWarnings("unchecked")
        List<Activity> activityList = (List<Activity>) cache.get(key, k -> {
            log.info("缓存未命中，查询Redis，userid: {}", userid);

            RedisActivityData redisActivityData = getRedisActivityData(k, userid);

            if (redisActivityData != null) {
                if (redisActivityData.getExpireTime().isAfter(LocalDateTime.now())) {
                    log.info("查询Redis,缓存未过期，直接返回，userid: {}", userid);
                    return redisActivityData.getActivityList();
                } else {
                    log.info("redis缓存已过期，尝试重建缓存，userid: {}", userid);
                    attemptCacheRebuild(redisActivityData, k, userid);
                    return redisActivityData.getActivityList();
                }
            }

            // Redis无数据，查询数据库并更新缓存
            return queryAndCacheFromDatabase(k, userid);
        });

        return Result.success(activityList);
    }

    private RedisActivityData getRedisActivityData(String key, Integer userid) {
        try {
//            String redisData = redisService.get(key);
            String redisData = stringRedisTemplate.opsForValue().get(key);
            return JSON.parseObject(redisData, RedisActivityData.class);
        } catch (Exception e) {
            log.error("Redis查询失败，用户ID: {}, 错误信息: {}", userid, e.getMessage());
            return null;
        }
    }



    /**
     * 开启新线程尝试重建缓存
     * @param redisActivityData
     * @param key
     * @param userid
     */
    private void attemptCacheRebuild(RedisActivityData redisActivityData, String key, Integer userid) {
        boolean lockAcquired = false;
        String lockKey = "lockKey"+userid;
        try {
            lockAcquired = tryLock(lockKey); // 尝试获取锁

            if (lockAcquired) {
                log.info("成功获取锁，开始重建缓存，userid: {}", userid);
                CACHE_REBUILD_EXECUTOR.submit(() -> rebuildCache(key, userid));
            } else {
                log.warn("未能获取锁，缓存重建正在进行中，userid: {}", userid);
            }
        } catch (Exception e) {
            log.error("获取锁时发生异常，用户ID: {}, 错误信息: {}", userid, e.getMessage());
        } finally {
            // 只有获取锁成功时才释放锁
            if (lockAcquired) {
                unlock(lockKey);
                log.info("锁释放成功，userid: {}", userid);
            }
        }
    }


    /**
     * 重建缓存
     * @param key
     * @param userid
     */
    private void rebuildCache(String key, Integer userid) {
        try {
            List<Activity> activityList = activityService.listById(userid);
            updateCache(key, activityList);
        } catch (Exception e) {
            log.error("缓存重建失败，用户ID: {}, 错误信息: {}", userid, e.getMessage());
        }
    }

    /**
     * 查询数据库并且更新缓存
     * @param key
     * @param userid
     * @return
     */
    private List<Activity> queryAndCacheFromDatabase(String key, Integer userid) {
        try {
            log.info("查询数据库，userid: {}", userid);
            List<Activity> activityList = activityService.listById(userid);
            updateCache(key, activityList);
            return activityList;
        } catch (Exception e) {
            log.error("数据库查询失败，用户ID: {}, 错误信息: {}", userid, e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * 更新缓存(逻辑过期时间，避免缓存击穿，Redis中一个热点key在失效的同时，大量的请求过来，从而会全部到达数据库，压垮数据库。)
     * @param key
     * @param activityList
     */
    private void updateCache(String key, List<Activity> activityList) {
        try {
            int randomSeconds = ThreadLocalRandom.current().nextInt(0, 20); // 生成随机数,避免缓存雪崩
            RedisActivityData redisActivityData = new RedisActivityData();
            redisActivityData.setActivityList(activityList);
            redisActivityData.setExpireTime(LocalDateTime.now().plusSeconds(CacheConstant.LOG_EXPIRETIME+randomSeconds));
//            redisService.set(key, JSON.toJSONString(redisActivityData));
            stringRedisTemplate.opsForValue().set(key,JSON.toJSONString(redisActivityData));
            cache.put(key, activityList);
            log.info("缓存更新成功，key: {}", key);
        } catch (Exception e) {
            log.error("缓存更新失败，key: {}, 错误信息: {}", key, e.getMessage());
        }
    }




    @PostMapping("/list")
    @Operation(summary = "根据条件查询活动")
    public Result queryActivities(@RequestBody QueryActivitiesDTO queryActivitiesDTO) {
        Integer num = queryActivitiesDTO.getNum();
        if(num==null||num<=-1){
            queryActivitiesDTO.setNum(null);
        }
        System.out.println(queryActivitiesDTO);
        //根据条件选择
        List<ActivityListVO> activityListVO = activityService.listByParams(queryActivitiesDTO);

        Map<String, Object> claims = ThreadLocalUtil.get();
        Integer userId = (Integer) claims.get("id");
        log.info("解析出来的id：{}",userId);
            List<Integer> activityIds = registrationService.findSubbyUserId(userId);
            for (ActivityListVO vo : activityListVO) {
                Long voId = vo.getId(); // 获取当前 ActivityListVO 对象的 ID
                // 将 activityIds 中的 Integer 转换为 Long，并进行比较
                if (activityIds.stream().map(Integer::longValue).collect(Collectors.toList()).contains(voId)) {
                    vo.setIsSub(1);  // 假设 setIsSub 是用来设置 isSub 属性的方法
                }
            }

        return Result.success("查找成功",activityListVO);
    }

    /**
     * 模糊查询活动名
     * @param title
     * @return
     */
    @GetMapping("/search")
    @Operation(summary = "模糊查询活动title")
    public Result searchActivities(@RequestParam String title) {
        log.info("活动名称:{}",title);
        List<Activity> activities = activityService.searchByTitle(title);
        return Result.success("查询成功",activities);
    }

    /**
     * 组合条件查询活动
     * @param startDate
     * @param endDate
     * @param location
     * @return
     */
    @GetMapping("/filter")
    @Operation(summary = "组合条件查询活动")
    public Result filterActivities(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date endDate,
            @RequestParam(required = false) String location) {
        log.info("开始组合条件查询活动: 开始日期={}, 结束日期={}, 地点={}", startDate, endDate, location);
        List<Activity> activities = activityService.filterActivities(startDate, endDate, location);
        return  Result.success("查询成功",activities);
    }




    /**
     * 分页查询所有活动
     * @param pageNo
     * @param pageSize
     * @return
     */
    @GetMapping("/pagelist")
    @Operation(summary = "分页查询所有活动")
    public Result<Page<Activity>> getActivitiesByPage(
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize) {
        log.info("pageno:{},pagesize:{}",pageNo,pageSize);
        Page<Activity> page = activityService.getActivitiesByPage(pageNo, pageSize);
        return Result.success("查询成功",page);
    }

}