package com.ivy.juc.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class InventoryService {


    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Value("${server.port}")
    String port;


    private Lock lock = new ReentrantLock();



    public String sale(){
        String res = "";

        lock.lock();
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
            lock.unlock();
        }


        return res + "\t" + "服务端口号" + port;
    }
}
