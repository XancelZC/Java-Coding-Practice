package com.practice.JdkDynamicProxy;

public class UserServiceImpl implements UserService{
    @Override
    public void save() {
        System.out.println("保存用户");
    }
}
