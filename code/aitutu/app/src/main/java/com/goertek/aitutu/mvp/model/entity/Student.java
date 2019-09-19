package com.goertek.aitutu.mvp.model.entity;

import org.litepal.crud.LitePalSupport;

public class Student extends LitePalSupport {

    private String name;

    private int age;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
