package com.bmi.sharedeal.service.utils;

import java.security.MessageDigest;
import java.util.Random;

import com.bmi.sharedeal.service.Config;

/**
 * 
 * @author xiaoyu
 * @date 2015/8/14.
 */
public class TextUtils {
	
	public static boolean isEmpty(String s) {
		return (s == null) || s.isEmpty();
	}
	
	public static String MD5(String s) {
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        try {
            byte[] btInput = s.getBytes();
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            mdInst.update(btInput);
            // 获得密文
            byte[] md = mdInst.digest();
            // 把密文转换成十六进制的字符串形式
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            if (Config.DEBUG) {
                e.printStackTrace();
            }
            return null;
        }
    }
	
	 /* 生成随机数字串
     *
     * @param length
     * @return
     */
    public static String getRandomCode(int length) {
        String str = "0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(str.length());
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }

    /**
     * 生成随机字符串
     *
     * @param length
     * @return
     */
    public static String getRandomStr(int length) {
        String str = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(str.length());
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }
}
