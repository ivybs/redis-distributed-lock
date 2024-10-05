package com.ivy.juc.thread;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;


/**
 *
 * 1需求说明
 * 1.1同一款产品，同时搜索出同款产品在各大电商平台的售价;
 * 1.2同一款产品，同时搜索出本产品在同一个电商平台下，各个入驻卖家售价是多少
 * 2 输出返回:
 * 出来结果希望是同款产品的在不同地方的价格清单列表，返回一个List<String>
 * 《mysql》 in jd price is 88.05
 * 《mysql》 in dangdang price is 86.11
 * 《mysql》in taobao price is 90.43
 * 3解决方案，比对同一个商品在各个平台上的价格，要求获得一个清单列表，
 * ① stepbystep，按部就班，查完京东查淘宝，查完淘宝查天猫.，
 * ② all in 万箭齐发，一口气多线程异步任务同时查询。。。。。
 * */

public class CompletableFutureMallDemo {

    private void getPrice(List<NetMall> netMalls){
        List<String> collect = netMalls.stream().map(netMall -> CompletableFuture.supplyAsync(() -> {
            return netMall.calPrice();
        })).collect(Collectors.toList()).stream().map(stringCompletableFuture -> {
            return stringCompletableFuture.join();
        }).collect(Collectors.toList());
    }
}

class NetMall{
    private String name;

    private String price;

    public String calPrice(){
        return String.valueOf(Math.random());
    }


}
