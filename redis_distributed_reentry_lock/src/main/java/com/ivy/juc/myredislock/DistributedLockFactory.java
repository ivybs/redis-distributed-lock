package com.ivy.juc.myredislock;


import cn.hutool.core.util.IdUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.locks.Lock;

@Component
public class DistributedLockFactory {
    @Autowired
    private StringRedisTemplate redisTemplate;

    private String lockName;

    private String uuid;

    public DistributedLockFactory() {
        this.uuid = IdUtil.simpleUUID();
    }

    public Lock getDistributedLock(String lockType){
        if (lockType == null) return null;
        if (lockType.equalsIgnoreCase("redis")){
            this.lockName = "zzyyRedisLock";
            return new RedisDistributedLock(redisTemplate,lockName,uuid);
        } else if (lockType.equalsIgnoreCase("zookeeperd")){
            this.lockName = "zzyyZookeeperLockNode";
            // todo zookeeper版本的分布式锁
            return null;
        }else if (lockType.equalsIgnoreCase("mysql")){
            this.lockName = "zzyyMySQLLock";
            // todo MYSQL实现的分布式锁
            return null;
        }
        return null;
    }

}
