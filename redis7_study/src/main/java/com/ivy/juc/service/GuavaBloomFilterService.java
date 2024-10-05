package com.ivy.juc.service;


import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;



/**
 * 1.缓存穿透 就是请求去查询一条数据，先查redis，redis里面没有，再查mysql，mysql里面无，
 * 都查询不到该条记录，但是请求每次都会打到数据库上面去，导致后台数据库压力暴增
 * 2.解决缓存穿透：
 *      a.布隆过滤器
 *      b.
 *
 * */
@Service
@Slf4j
public class GuavaBloomFilterService {
    // 1.定义一个常量
    public static final int _1W = 10000;
    // 2.定义我们guava布隆过滤器，初始容量
    public static final int SIZE = 100 * _1W;
    // 3.误判率，它越小误判的个数也越少（思考：是否可以无限小？ 没有误判岂不是更好）
    public static double fpp = 0.01;  // 这个数越小所用的hash函数越多，bitmap占用的位越多  默认的就是0.03，5个hash函数   0.01，7个函数
    // 4.创建guava布隆过滤器
    private static BloomFilter<Integer> bloomFilter = BloomFilter.create(Funnels.integerFunnel(), SIZE, fpp);


    public void guavaBloomFilter(){

        // 1.向布隆过滤器中插入100w条白名单数据
        for (int i = 0; i < SIZE; i++) {
            bloomFilter.put(i);
        }

        // 2. 故意取10w个不在白名单中的数据，来进行误判率演示
        ArrayList<Integer> list = new ArrayList<>(10 * _1W);

        // 3. 验证
        for (int i = SIZE + 1; i < SIZE + (10 * _1W); i++) {
            if (bloomFilter.mightContain(i)){
                log.info("被误判了：{}",i);
                list.add(i);
            }
        }
        log.info("误判总数量：{}", list.size());

    }

}
