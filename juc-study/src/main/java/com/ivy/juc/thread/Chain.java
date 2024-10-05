package com.ivy.juc.thread;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

public class Chain {
    public static void main(String[] args) {
        Student student = new Student();
        student.setAge("20").setName("tom").setMajor("cs");
    }
}


@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
class Student{
    private String name;
    private String major;
    private String age;
}

