package com.ivy.juc.lock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class InterruptDemo {

    static volatile boolean isStop = false;
    static AtomicBoolean atomicBoolean = new AtomicBoolean(false);
    public static void main(String[] args) {


    }


    private static void m2(){
        Thread t1 = new Thread(() -> {
            while (true){
                // 判断是否被标识为中断
                if (Thread.currentThread().isInterrupted()){
                    System.out.println(Thread.currentThread().getName()+"\t isStop改为true,程序停止");
                    break;
                }
                System.out.println("----------hello volatile");
            }
        },"t1");
        t1.start();


        try{  TimeUnit.MILLISECONDS.sleep(500); }catch (InterruptedException e ){e.printStackTrace();}

        new Thread(() -> {
//            isStop = true;
            // 标识另一个线程为中断
            t1.interrupt();
        }).start();

    }


    private static void m1(){
        // 你给我一个要中断我自己的标志位，我读到了，我就中断我自己
        new Thread(() -> {
            while (true){
//                if (isStop){
                if (atomicBoolean.get()){
                    System.out.println(Thread.currentThread().getName()+"\t isStop改为true,程序停止");
                    break;
                }
                System.out.println("----------hello volatile");
            }
        },"t1").start();

        try{  TimeUnit.MILLISECONDS.sleep(500); }catch (InterruptedException e ){e.printStackTrace();}

        new Thread(() -> {
//            isStop = true;
            atomicBoolean.set(true);
        }).start();
    }
}
