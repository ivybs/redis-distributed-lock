package com.ivy.juc;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class ReEntryLockDemo {

    public void entry01(){
        final Object obj = new Object();
        new Thread(()->{
            synchronized (obj){
                System.out.println(Thread.currentThread().getName()+"外层调用");
                synchronized (obj){
                    System.out.println(Thread.currentThread().getName()+"中层调用");
                    synchronized (obj){
                        System.out.println(Thread.currentThread().getName()+"内 层调用");

                    }
                }

            }
        }).start();
    }



    public void entry02(){
        ReentrantLock reentrantLock = new ReentrantLock();
        new Thread(()->{
            reentrantLock.lock();
            try {
                System.out.println(Thread.currentThread().getName()+"外层调用");
                reentrantLock.lock();
                try {
                    System.out.println(Thread.currentThread().getName()+"内层调用");

                }finally {
                    reentrantLock.unlock();
                }

            }finally {
                reentrantLock.unlock();
            }

        },"t1").start();

        try {
            TimeUnit.MILLISECONDS.sleep(2);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        new Thread(()->{
            reentrantLock.lock();
            try {
                System.out.println(Thread.currentThread().getName()+"外层调用");
                reentrantLock.lock();
                try {
                    System.out.println(Thread.currentThread().getName()+"内层调用");

                }finally {
                    reentrantLock.unlock();
                }

            }finally {
                reentrantLock.unlock();
            }
        },"t2").start();
    }



    public static void main(String[] args) {

    }
}
