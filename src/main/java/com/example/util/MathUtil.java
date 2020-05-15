package com.example.util;

public class MathUtil {
    /**
     * 十进制 转 8位 2进制
     * @param n
     */
    public static void binaryToDecimal(int n){
        int t = 0;  //用来记录位数
        int bin = 0; //用来记录最后的二进制数
        int r = 0;  //用来存储余数
        while(n != 0){
            r = n % 2;
            n = n / 2;
            bin += r * Math.pow(10,t);
            t++;
        }
        System.out.println(String.format("%08d",bin));
    }


    public static void main(String[] args) {
        int i = Integer.parseInt("12", 16);
        System.out.println(i);
    }

}
