package com.mail.login;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CodeCache {

    private static final long EXPIRE_MILLIS = 5 * 60 * 1000;
    private Map<String, CodeInfo> cache = new ConcurrentHashMap<>();

    /**
     * 存 邮箱与验证码之间的关系
     * 取 校验 验证码是否正确
     * 设置 验证码过期时间
     */

    //存 验证码
    public void saveCode(String email,String code){
        long expireAt = System.currentTimeMillis() + EXPIRE_MILLIS;
        cache.put(email,new CodeInfo(code,expireAt));
    }

    //校验 验证码
    public boolean verifyCode(String email,String inputCode){
        CodeInfo info = cache.get(email);
        if (info == null){
            return false;
        }
        if (System.currentTimeMillis() > info.expireTime){
            cache.remove(email);
            return false;
        }
        boolean match = info.code.equals(inputCode);
        if (match){
            cache.remove(email);
        }
        return match;
    }

    private class CodeInfo {
        String code;
        long expireTime;

        public CodeInfo(String code, long expireTime) {
            this.code = code;
            this.expireTime = expireTime;
        }
    }
}
