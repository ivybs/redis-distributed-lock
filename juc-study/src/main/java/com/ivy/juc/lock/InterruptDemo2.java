package com.ivy.juc.lock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;

public class InterruptDemo2 {
    public static void main(String[] args) {
        Thread thread = new Thread(() -> {
            for (int i = 0; i < 300; i++) {
                System.out.println("--------" + i);
            }
            System.out.println("t1在线程调用interrupt()后的中断标识02" + Thread.currentThread().isInterrupted());
        },"t1");
        thread.start();

        System.out.println("t1在默认的中断标识："+thread.isInterrupted());

        try{  TimeUnit.MILLISECONDS.sleep(500); }catch (InterruptedException e ){e.printStackTrace();}
        thread.interrupt();
        System.out.println("t1线程调用interrupt()后的中断标识01" + thread.isInterrupted());

        try{  TimeUnit.MILLISECONDS.sleep(500); }catch (InterruptedException e ){e.printStackTrace();}
        System.out.println("t1线程调用interrupt()后的中断标识03" + thread.isInterrupted());
    }
}
