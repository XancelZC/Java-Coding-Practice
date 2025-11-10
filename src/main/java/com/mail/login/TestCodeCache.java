package com.mail.login;

public class TestCodeCache {
    public static void main(String[] args) throws InterruptedException {
        CodeCache cache = new CodeCache();

        // 模拟发验证码
        cache.saveCode("test@mail.com", "123456");

        // 验证正确
        System.out.println(cache.verifyCode("test@mail.com", "123456")); // true

        // 验证重复使用
        System.out.println(cache.verifyCode("test@mail.com", "123456")); // false

        // 模拟过期
        cache.saveCode("a@mail.com", "000000");
        Thread.sleep(5 * 60); // 等过期（5分钟）
        System.out.println(cache.verifyCode("a@mail.com", "000000")); // false
    }
}

