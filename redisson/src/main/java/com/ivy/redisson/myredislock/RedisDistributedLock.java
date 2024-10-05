package com.ivy.redisson.myredislock;

import lombok.Data;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;


/**
 * 自研的redis分布式锁
 * */
@Data
public class RedisDistributedLock implements Lock {

    private StringRedisTemplate redisTemplate;
    private String lockKeyName; //KEYS[1]
    private String uuidValue; // ARGV[1]
    private long expireTime; // ARGV[2]


//    public RedisDistributedLock(StringRedisTemplate redisTemplate, String lockKeyName) {
//        this.redisTemplate = redisTemplate;
//        this.lockKeyName = lockKeyName;
//        this.uuidValue = IdUtil.simpleUUID()+":"+Thread.currentThread().getId();
//        this.expireTime = 10;
//    }


    public RedisDistributedLock(StringRedisTemplate redisTemplate, String lockKeyName, String uuidValue) {
        this.redisTemplate = redisTemplate;
        this.lockKeyName = lockKeyName;
        this.uuidValue = uuidValue+":"+Thread.currentThread().getId();
        this.expireTime = 30L;
    }

    @Override
    public void lock() {
        tryLock();
    }



    @Override
    public boolean tryLock() {
        try {
            tryLock(-1L,TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        if (time == -1){
            System.out.println("lock   uuid:" + this.uuidValue);
            // 加锁的lua脚本
            String lockScript = "if redis.call('exists',KEYS[1]) == 0 or redis.call('hexists',KEYS[1],ARGV[1]) == 1 then  " +
                                    "redis.call('hincrby',KEYS[1],ARGV[1],1) " +
                                    "redis.call('expire',KEYS[1],ARGV[2]) " +
                                    "return 1 " +
                            " else return 0  " +
                            "  end";
            while (!redisTemplate.execute(new DefaultRedisScript<>(lockScript,Boolean.class), Arrays.asList(lockKeyName),uuidValue,String.valueOf(expireTime))){
                // 没有获取到锁的时候，就先暂停一下再重试获取锁
                try{  TimeUnit.MILLISECONDS.sleep(10); }catch (InterruptedException e ){e.printStackTrace();}
            }
            // 加锁成功后，要新建一个后台扫描程序，来监视Key目前的ttl，是否到我们规定的超时时间的1/3 ，如果超过了要实现自动续期
            resetLockExpireTime();
            return true;
        }
        return false;
    }

    private void resetLockExpireTime() {
        String script = "if redis.call('HEXISTS',KEYS[1],ARGV[1]) == 1 then  " +
                            "return redis.call('expire',KEYS[1],ARGV[2])  " +
                        "else  " +
                            "return 0  " +
                        " end";
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Boolean execute = redisTemplate.execute(new DefaultRedisScript<>(script, Boolean.class), Arrays.asList(lockKeyName), uuidValue,String.valueOf(30));
                if (execute){
                    resetLockExpireTime();
                }
            }
        },(this.expireTime * 1000)/3);
    }


    @Override
    public void unlock() {
        System.out.println("unlock   uuid:" + this.uuidValue);

        String unlockScript = "if redis.call('hexists',KEYS[1],ARGV[1]) == 0  then " +
                                    "return nil  " +
                              "elseif redis.call('hincrby',KEYS[1],ARGV[1],-1) == 0 then  " +
                                    "return redis.call('del',KEYS[1]) " +
                              "else " +
                                    "return 0 " +
                              "end";

        Long execute = redisTemplate.execute(new DefaultRedisScript<>(unlockScript, Long.class), Arrays.asList(lockKeyName), uuidValue);
        // nil == false   1 == true   0 == false
        if (null == execute){
            throw new RuntimeException("this lock does not exist");
        }

    }









    // ---------------------下面两个暂时用不到 不进行重写--------------------

    @Override
    public Condition newCondition() {
        return null;
    }
    @Override
    public void lockInterruptibly() throws InterruptedException {

    }
}
