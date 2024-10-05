package com.ivy.redisson;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;



@SpringBootApplication
public class RedisDistributedLockApplicationStarter {
    public static void main(String[] args) {
        SpringApplication.run(RedisDistributedLockApplicationStarter.class,args);
    }
}
