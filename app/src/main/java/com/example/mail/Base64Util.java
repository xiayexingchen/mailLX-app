package com.example.mail;

import android.util.Base64;  // 使用 Android 的 Base64
import java.nio.charset.StandardCharsets;

/**
 * @author 曾佳宝
 */
public class Base64Util {

    /**
     * 编码
     *
     * @param data 加密的内容
     * @return 加密后的字符串
     */
    public static String encodeByBase64(String data) {
        // 将字符串转换为字节数组并进行 Base64 编码
        return Base64.encodeToString(data.getBytes(StandardCharsets.UTF_8), Base64.NO_WRAP);
    }

    /**
     * 解码
     *
     * @param data 解码的内容（Base64编码的字符串）
     * @return 解码后的内容
     */
    public static String decodeByBase64(String data) {
        byte[] decodedBytes = Base64.decode(data, Base64.NO_WRAP);  // 使用 Android Base64 解码
        return new String(decodedBytes, StandardCharsets.UTF_8);  // 转为字符串
    }
}
