package com.mail.login.service;

import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class CodeGeneratorService  {
    private static final int DEFAULT_LENGTH = 6;
    private static final String DIGITS = "0123456789";

    public static String generate(){
        return generate(DEFAULT_LENGTH);
    }

    public static String generate(int length) {
        StringBuilder code = new StringBuilder(length);
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            code.append(DIGITS.charAt(random.nextInt(DIGITS.length())));
        }
        return code.toString();
    }
}
