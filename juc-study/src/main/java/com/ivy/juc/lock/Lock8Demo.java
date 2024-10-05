package com.ivy.juc.lock;

import io.lettuce.core.ScriptOutputType;

class Phone{

    public  synchronized void sendEmail(){
        System.out.println("------------sendEmail");
    }


    public  synchronized void sendSMS(){
        System.out.println("------------sendSMS");
    }

    public static synchronized void callSomeOne(){
        System.out.println("-------------callSomeOne");
    }


    public void hello(){
        System.out.println("-------------hello");
    }
}


public class Lock8Demo {
    public static void main(String[] args) {

    }
}
