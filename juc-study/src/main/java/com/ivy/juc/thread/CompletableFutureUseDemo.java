package com.ivy.juc.thread;

import java.util.concurrent.*;

public class CompletableFutureUseDemo {

    public static void main(String[] args){
        CompletableFuture<String> stringCompletableFuture = CompletableFuture.supplyAsync(()->{
            return "hello";
        });
        // join在编译期间不会去管异常，get在编译期间就要求把异常抓住
        // 其他join和get是一样的作用
        System.out.println(stringCompletableFuture.join());


    }

    private static void fixPool(){
        ExecutorService threadPool = Executors.newFixedThreadPool(3);
        try {
            CompletableFuture<Integer> integerCompletableFuture = CompletableFuture.supplyAsync(() -> {
                System.out.println(Thread.currentThread().getName() + "-----------come in");
                int res = ThreadLocalRandom.current().nextInt();
                try {
                    TimeUnit.MILLISECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("----1s后结果" + res);
                return res;
            },threadPool).whenComplete((v,e) ->{
                if (e == null){
                    System.out.println("-------计算完成");

                }
            }).exceptionally(e ->{
                e.printStackTrace();
                return null;
            });
            System.out.println(Thread.currentThread().getName()+"做其他的任务");

        }catch (Exception e){
            e.printStackTrace();
        }
    }




    private static void forkJoin(){
        CompletableFuture<Integer> integerCompletableFuture = CompletableFuture.supplyAsync(() -> {
            System.out.println(Thread.currentThread().getName() + "-----------come in");
            int res = ThreadLocalRandom.current().nextInt();
            try {
                TimeUnit.MILLISECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("----1s后结果" + res);
            return res;
        }).whenComplete((v,e) ->{
            if (e == null){
                System.out.println("-------计算完成");

            }
        }).exceptionally(e ->{
            e.printStackTrace();
            return null;
        });

        System.out.println(Thread.currentThread().getName()+"做其他的任务");

        // 主线程结束的太快，会导致异步任务还没有结束，主线程就关闭，主线程关闭后，forkJoin线程池线程也会随之关闭
        try{  TimeUnit.MILLISECONDS.sleep(500); }catch (InterruptedException e ){e.printStackTrace();}

    }


    private static void future() throws ExecutionException, InterruptedException {
        CompletableFuture<Integer> integerCompletableFuture = CompletableFuture.supplyAsync(() -> {
            System.out.println(Thread.currentThread().getName() + "-----------come in");
            int res = ThreadLocalRandom.current().nextInt();
            try {
                TimeUnit.MILLISECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("----1s后结果" + res);
            return res;
        });

        System.out.println(Thread.currentThread().getName()+"做其他的任务");
        System.out.println(integerCompletableFuture.get());

    }
}
