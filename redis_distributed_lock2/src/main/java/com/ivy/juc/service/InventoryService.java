package com.ivy.juc.service;


import cn.hutool.core.util.IdUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

@Service
public class InventoryService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Value("${server.port}")
    String port;




    /**
     * 分布式锁的主要问题：
     * 1.我的锁只有我能删除  方法：锁的key里面带有个uuid
     * 2.锁要有超时时间，否则容易导致死锁  方法：给锁设置超时时间
     * 3.锁要可以重入
     * 4.如果是乐观锁，还需要加个版本号
     *
     *
     *
     * */



    // 当前版本不满足锁的可重入性
    public String sale(){
        String res = "";
        String key = "zzyyRedisLock";
        String uuidValue = IdUtil.simpleUUID()+":"+Thread.currentThread().getId();
        // 抢到锁之后立刻给锁加过期时间
        while (Boolean.FALSE.equals(stringRedisTemplate.opsForValue().setIfAbsent(key, uuidValue, 30L, TimeUnit.SECONDS))){
            try{
                TimeUnit.MILLISECONDS.sleep(20);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }
        // 抢到锁之后立刻给锁加过期时间
        // 这里不能这样写是因为两个指令之间有间隔，比如在这里设置超时时间，然后在上面的trycatch语句中，某个线程挂了
        // 在还没来得及设置超时时间的时候线程挂了，那结果和上一版本是一样的
//        stringRedisTemplate.expire(key,30L,TimeUnit.SECONDS);
        // 抢锁成功的请求线程，进行正常的业务逻辑操作，扣减库存
        try {
            // 1. 查询库存信息
            String inventory001 = stringRedisTemplate.opsForValue().get("inventory001");
            // 2.判断库存是否足够
            Integer inventoryNumber = inventory001 == null ? 0 : Integer.parseInt(inventory001);
            // 3.扣减库存
            if (inventoryNumber > 0){
                stringRedisTemplate.opsForValue().set("inventory001",String.valueOf(--inventoryNumber));
                res = "成功卖出一个商品，仓库剩余：" + inventoryNumber;
                System.out.println(res + "\t" + "服务端口号" + port);
            } else {
                res = "商品买完了";

            }
        }catch (Exception e){

        }finally {
            // if redis.call('get',KEYS[1]) == ARGV[1] then return redis.call('del',KEYS[1]) else return 0 end
            // 使用Lua脚本，实现查询和删除两个操作合为一个操作，保证了查询删除操作的原子性
            String luaScirpt = "" +
                    "if redis.call('get',KEYS[1]) == ARGV[1] then " +
                        "return redis.call('del',KEYS[1]) " +
                    "else " +
                        "return 0 " +
                    "end";

            stringRedisTemplate.execute(new DefaultRedisScript<>(luaScirpt,Boolean.class), Collections.singletonList(key),uuidValue);


        }


        return res + "\t" + "服务端口号" + port;

    }



    /**
    * V 5.0 防止线程A删除掉线程B的key
     *   大致的场景：
     *   线程A执行任务超时，即线程A占用的锁超时时间为30s，但是实际线程A任务执行了32秒，在第30秒时，锁被释放，
     *   然后第31秒时，线程B获取到了锁，线程B正常执行自己的任务，
     *   在第32s时，线程A执行完任务，将锁删除掉
     *
     *   最终结果就是，等到线程B执行完自己手头上的任务，准备去释放锁的时候，发现自己原本要释放的锁资源已经不在了
     *
     *   修改后仍然存在的问题：
     *      最后的查询和删除锁的操作不是原子操作，需要用Lua脚本进行修改
    */




    /**
     * V 4.1 给Key加过期时间
     * */
    public String sale3(){
        String res = "";
        String key = "zzyyRedisLock";
        String uuidValue = IdUtil.simpleUUID()+":"+Thread.currentThread().getId();
        // 抢到锁之后立刻给锁加过期时间
        while (!stringRedisTemplate.opsForValue().setIfAbsent(key,uuidValue,30L,TimeUnit.SECONDS)){
            try{
                TimeUnit.MILLISECONDS.sleep(20);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }
        // 抢到锁之后立刻给锁加过期时间
        // 这里不能这样写是因为两个指令之间有间隔，比如在这里设置超时时间，然后在上面的trycatch语句中，某个线程挂了
        // 在还没来得及设置超时时间的时候线程挂了，那结果和上一版本是一样的
//        stringRedisTemplate.expire(key,30L,TimeUnit.SECONDS);
        // 抢锁成功的请求线程，进行正常的业务逻辑操作，扣减库存
        try {
            // 1. 查询库存信息
            String inventory001 = stringRedisTemplate.opsForValue().get("inventory001");
            // 2.判断库存是否足够
            Integer inventoryNumber = inventory001 == null ? 0 : Integer.parseInt(inventory001);
            // 3.扣减库存
            if (inventoryNumber > 0){
                stringRedisTemplate.opsForValue().set("inventory001",String.valueOf(--inventoryNumber));
                res = "成功卖出一个商品，仓库剩余：" + inventoryNumber;
                System.out.println(res + "\t" + "服务端口号" + port);
            } else {
                res = "商品买完了";

            }
        }catch (Exception e){

        }finally {
            stringRedisTemplate.delete(key);
        }


        return res + "\t" + "服务端口号" + port;

    }




    /**
     * v3.2版本，使用自旋代替递归
     *   当前的分布式锁没有过期时间，会导致的问题：
     *      线程A占有了当前的锁，然后A服务挂了，或者A任务执行完成后，没有把锁还回去，
     *      就会导致除了A线程以外的所有线程永远都在阻塞等待这把锁
     * */
    public String sale2(){
        String res = "";
        String key = "zzyyRedisLock";
        String uuidValue = IdUtil.simpleUUID()+":"+Thread.currentThread().getId();
        while (!stringRedisTemplate.opsForValue().setIfAbsent(key,uuidValue)){

            try{
                TimeUnit.MILLISECONDS.sleep(20);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }
        // 抢锁成功的请求线程，进行正常的业务逻辑操作，扣减库存
        try {
            // 1. 查询库存信息
            String inventory001 = stringRedisTemplate.opsForValue().get("inventory001");
            // 2.判断库存是否足够
            Integer inventoryNumber = inventory001 == null ? 0 : Integer.parseInt(inventory001);
            // 3.扣减库存
            if (inventoryNumber > 0){
                stringRedisTemplate.opsForValue().set("inventory001",String.valueOf(--inventoryNumber));
                res = "成功卖出一个商品，仓库剩余：" + inventoryNumber;
                System.out.println(res + "\t" + "服务端口号" + port);
            } else {
                res = "商品买完了";

            }
        }catch (Exception e){

        }finally {
            stringRedisTemplate.delete(key);
        }


        return res + "\t" + "服务端口号" + port;

    }




        /**
         *
         * V3.1版本，完成了递归重试，但是递归会很容易导致OOM的问题，因此需要进一步改进
         *  另外 高并发唤醒后推荐使用while判断而不是if
         * */
    public String sale1(){
        String res = "";
        String key = "zzyyRedisLock";
        String uuidValue = IdUtil.simpleUUID()+":"+Thread.currentThread().getId();
        Boolean flag = stringRedisTemplate.opsForValue().setIfAbsent(key,uuidValue);

        // 抢不到的线程要继续重试
        if (!flag){
            // 暂停20毫秒，进行递归重试
            try{
                TimeUnit.MILLISECONDS.sleep(20);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
            // 递归容易导致stackoverflow
            sale1();
        }else {
            // 抢锁成功的请求线程，进行正常的业务逻辑操作，扣减库存
            try {
                // 1. 查询库存信息
                String inventory001 = stringRedisTemplate.opsForValue().get("inventory001");
                // 2.判断库存是否足够
                Integer inventoryNumber = inventory001 == null ? 0 : Integer.parseInt(inventory001);
                // 3.扣减库存
                if (inventoryNumber > 0){
                    stringRedisTemplate.opsForValue().set("inventory001",String.valueOf(--inventoryNumber));
                    res = "成功卖出一个商品，仓库剩余：" + inventoryNumber;
                    System.out.println(res + "\t" + "服务端口号" + port);
                } else {
                    res = "商品买完了";

                }
            }catch (Exception e){

            }finally {
                stringRedisTemplate.delete(key);
            }

        }


        return res + "\t" + "服务端口号" + port;
    }
}
