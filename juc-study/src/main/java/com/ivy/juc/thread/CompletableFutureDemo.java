package com.ivy.juc.thread;

import java.util.concurrent.*;

public class CompletableFutureDemo {


    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // 如果不传入哪种线程池，默认是forkthreadPool
        ExecutorService threadPool = Executors.newFixedThreadPool(3);



        CompletableFuture<String> stringCompletableFuture = CompletableFuture.supplyAsync(() -> {
            System.out.println(Thread.currentThread().getName());
            try{  TimeUnit.MILLISECONDS.sleep(100); }catch (InterruptedException e ){e.printStackTrace();}
            return "hello supplyAsync";
        });
        System.out.println(stringCompletableFuture.get());

        CompletableFuture<Void> voidCompletableFuture = CompletableFuture.runAsync(() -> {
            System.out.println(Thread.currentThread().getName());
            try{  TimeUnit.MILLISECONDS.sleep(100); }catch (InterruptedException e ){e.printStackTrace();}

        });
        System.out.println(voidCompletableFuture.get());

        threadPool.shutdown();
    }

    private static void callable(){
            ExecutorService executor = Executors.newCachedThreadPool();

            Callable<Integer> task = () -> {
                // 这里是你的任务代码
                System.out.println("Callable task is running.");
                return 42; // 返回计算结果
            };

            Future<Integer> future = executor.submit(task);

            try {
                System.out.println("The result is: " + future.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            } finally {
                executor.shutdown();
            }
    }

    private static void futureTask() throws ExecutionException, InterruptedException {
        ExecutorService threadPool = Executors.newFixedThreadPool(3);



        FutureTask futureTask1 = new FutureTask(() ->{
            try{  TimeUnit.MILLISECONDS.sleep(500); }catch (InterruptedException e ){e.printStackTrace();}
            return "task1 over";
        });
        new Thread(futureTask1);
        threadPool.submit(futureTask1);
        System.out.println(futureTask1.get());

        FutureTask futureTask2 = new FutureTask(() ->{
            try{  TimeUnit.MILLISECONDS.sleep(500); }catch (InterruptedException e ){e.printStackTrace();}
            return "task2 over";
        });


        threadPool.submit(futureTask2);
        System.out.println(futureTask2.get());

        try{  TimeUnit.MILLISECONDS.sleep(500); }catch (InterruptedException e ){e.printStackTrace();}


        threadPool.shutdown();
    }

    private static void m1(){
        long starTime = System.currentTimeMillis();
        try{  TimeUnit.MILLISECONDS.sleep(500); }catch (InterruptedException e ){e.printStackTrace();}
        try{  TimeUnit.MILLISECONDS.sleep(300); }catch (InterruptedException e ){e.printStackTrace();}
        try{  TimeUnit.MILLISECONDS.sleep(300); }catch (InterruptedException e ){e.printStackTrace();}

        long endTime = System.currentTimeMillis();
        System.out.println("-------costTime:" + (endTime-starTime) + "ms");
        System.out.println(Thread.currentThread().getName()+ "\t ------end");
    }









//    public static void main(String[] args) throws ExecutionException, InterruptedException {
//
//        FutureTask<String> futureTask = new FutureTask<>(new MyThread2());
//        Thread t1 = new Thread(futureTask,"t1");
//        t1.start();
//        System.out.println(futureTask.get());
//
//    }



}




class MyThread2 implements Callable<String>{

    @Override
    public String call() throws Exception {
        System.out.println("-------------come in call()");
        return "hello Callable";
    }
}