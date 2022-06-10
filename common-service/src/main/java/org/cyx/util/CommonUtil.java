package org.cyx.util;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @Description CommonUtil
 * @Author cyx
 * @Date 2021/2/15
 **/
@Slf4j
public class CommonUtil {
    private static final String ALL_CHAR_NUM = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    /**
     * 获取ip
     *
     * @param request
     * @return
     */
    public static String getIpAddr(HttpServletRequest request) {
        String ipAddress = null;
        try {
            // 获取真实客户ip
            ipAddress = request.getHeader("x-forwarded-for");
            if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getHeader("Proxy-Client-IP");
            }
            if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getHeader("WL-Proxy-Client-IP");
            }
            if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getRemoteAddr();
                if (ipAddress.equals("127.0.0.1")) {
                    // 根据网卡取本机配置的IP
                    InetAddress inet = null;
                    try {
                        inet = InetAddress.getLocalHost();
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    }
                    ipAddress = inet.getHostAddress();
                }
            }
            // 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
            if (ipAddress != null && ipAddress.length() > 15) {
                // "***.***.***.***".length()
                // = 15
                if (ipAddress.indexOf(",") > 0) {
                    ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
                }
            }
        } catch (Exception e) {
            ipAddress = "";
        }
        return ipAddress;
    }


    public static String MD5(String data) {
        try {
            java.security.MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] array = md.digest(data.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte item : array) {
                sb.append(Integer.toHexString((item & 0xFF) | 0x100).substring(1, 3));
            }

            return sb.toString().toUpperCase();
        } catch (Exception exception) {
        }
        return null;
    }

    // 生成随机验证码
    public static String getRandomCode(int length) {
        String source = "0123456789";
        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            stringBuilder.append(source.charAt(random.nextInt(9)));
        }
        return stringBuilder.toString();
    }

    public static long getCurrentTimeStamp() {
        return System.currentTimeMillis();
    }

    public static String getRandomString(int length) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(ALL_CHAR_NUM.charAt(random.nextInt(ALL_CHAR_NUM.length())));
        }
        return sb.toString();
    }

    public static void sengMsg(HttpServletResponse response, Object obj) {
        ObjectMapper objectMapper = new ObjectMapper();

        try (PrintWriter printWriter = response.getWriter();) {
            response.setContentType("application/json;charset=utf-8");
            printWriter.print(objectMapper.writeValueAsString(obj));
            response.flushBuffer();
        } catch (IOException e) {
            log.error("返回给前端异常");
        }
    }

    public static Map<String, Object> iPage2Map(IPage iPage, Object data) {
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("total_record", iPage.getTotal());
        resultMap.put("total_page", iPage.getPages());
        resultMap.put("current_data", data);
        resultMap.put("current_page", iPage.getCurrent());
        resultMap.put("page,size", iPage.getSize());
        return resultMap;
    }

}
