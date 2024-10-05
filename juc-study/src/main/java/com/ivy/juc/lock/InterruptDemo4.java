package com.ivy.juc.lock;

public class InterruptDemo4 {
    public static void main(String[] args) {

        /**
         *  Thread.interrupted(); : 静态方法，执行完只会把标识中断标识重置为false
         *              即 最开始 interrupt: true
         *                  Thread.interrupted()后，返回true,并重置interrupt:false
         * Thread.currentThread().isInterrupted(); 只会返回当前线程的中断标识状态
          *
         *
         *
         *
         * */



        Thread.interrupted();
        Thread.currentThread().isInterrupted();

        System.out.println(Thread.currentThread().getName()+"\t"+Thread.interrupted());
        System.out .println(Thread.currentThread().getName()+"\t"+Thread.interrupted());
        System.out.println("----1");
        Thread.currentThread().interrupt();//中断标志位设置为true
        System.out.println("----2");
        System.out .println(Thread.currentThread().getName()+"\t"+Thread.interrupted());
        System.out.println(Thread.currentThread().getName()+"\t"+Thread.interrupted());
    }
}
