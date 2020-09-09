package com.bzh;

import java.util.UUID;

public class TestUUID {

    public static void main(String[] args) {
        String s = UUID.randomUUID().toString();
        System.out.println(s);
        String jepg = "jepg";
        String s2 = UUID.randomUUID().toString().replaceAll("-", "") + "." + jepg;
        System.out.println(s2);
    }
}
