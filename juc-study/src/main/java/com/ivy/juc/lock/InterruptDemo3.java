package com.ivy.juc.lock;

import java.util.concurrent.TimeUnit;

public class InterruptDemo3 {


    /**
     * 1 中断标志位，默认false
     * 2 t2---->t1发出了中断协腐，t2调用t1.interrupt()，中断标志位true
     * 3 中断标志位true，正常情况，程序停止，^^
     * 4 中断标志true，异常情况,InterruptedException，将会把中断状态将被清除，并且将收到InterruptedException 。中断标志位false导致无限循环
     * 5 在catch块中，需要再次给中断标志位设置为true，2次调用停止程序才0K
     * */


    public static void main(String[] args) {
        Thread t1 = new Thread(() -> {
            while (true){
                // 判断是否被标识为中断
                if (Thread.currentThread().isInterrupted()){
                    System.out.println(Thread.currentThread().getName()+"\t isStop改为true,程序停止");
                    break;
                }
                // 睡一会
                // 会导致抛出interruptedException 并且一直在循环
                try{  TimeUnit.MILLISECONDS.sleep(500); }
                catch (InterruptedException e ){
                    e.printStackTrace();
                    Thread.currentThread().interrupt();// 这里为什么要再中断一次
                }
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
}
