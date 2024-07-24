package org.salt.ai.hub.frame.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Slf4j
public class EncipherUtil {

    public static String MD5(String input) {
        try {
            // 获取 MD5 摘要算法的 MessageDigest 实例
            MessageDigest md = MessageDigest.getInstance("MD5");

            // 将输入字符串转换为字节数组
            byte[] bytes = input.getBytes();

            // 计算摘要
            byte[] digest = md.digest(bytes);

            // 将字节数组转换为十六进制字符串
            StringBuilder result = new StringBuilder();
            for (byte b : digest) {
                result.append(String.format("%02x", b));
            }

            return result.toString();

        } catch (NoSuchAlgorithmException e) {
            log.error("calculateMD5 error, e:{}", e.getMessage());
            return null;
        }
    }
}
