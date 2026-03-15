package com.cloud.baowang.util;

import cn.hutool.core.util.ObjectUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;

public class SignUtil {
    private static final Logger logger = LoggerFactory.getLogger(SignUtil.class);

    public static String paramSignsPay(Map<String, Object> hashMap, String key) {
        String signStr = getSignVal(hashMap, key);
        String signVal = DigestUtils.md5DigestAsHex(signStr.getBytes()).toLowerCase();
        logger.debug("签名原始字符串:{},签名值:{}", signStr, signVal);
        return signVal;
    }

    public static String signOriginStr(String signStr) {
        String signVal = DigestUtils.md5DigestAsHex(signStr.getBytes()).toLowerCase();
        logger.debug("signOriginStr 签名原始字符串:{},签名值:{}", signStr, signVal);
        return signVal;
    }


   /* public static String getSignStr(Map<String, Object> hashMap, String key) {
        Map<String, Object> cont = new TreeMap<>();
        Iterator<String> iter = hashMap.keySet().iterator();
        while (iter.hasNext()) {
            String paramKey = iter.next();
            cont.put(paramKey, hashMap.get(paramKey));
        }

        StringBuffer queryStr = new StringBuffer();
        Iterator<String> iterator = cont.keySet().iterator();
        while (iterator.hasNext()) {
            String cutKey = iterator.next();
            if (!cutKey.equals("sign") && !cutKey.equals("ip") && !cutKey.equals("remark")) {
                if (cont.get(cutKey) != null && !cont.get(cutKey).equals("")) {
                    if (queryStr.toString().equals("")) {
                        queryStr.append(cutKey).append("=").append(cont.get(cutKey));
                    } else {
                        queryStr.append("&").append(cutKey).append("=").append(cont.get(cutKey));
                    }
                }
            }
        }
        if (ObjectUtil.isNotEmpty(key)) {
            queryStr.append("&key=" + key);
        }

        return queryStr.toString();
    }*/

    public static String getSignVal(Map<String, Object> hashMap, String key) {
        Map<String, Object> cont = new TreeMap<>();
        Iterator<String> iter = hashMap.keySet().iterator();
        while (iter.hasNext()) {
            String paramKey = iter.next();
            cont.put(paramKey, hashMap.get(paramKey));
        }

        StringBuffer queryStr = new StringBuffer();
        Iterator<String> iterator = cont.keySet().iterator();
        while (iterator.hasNext()) {
            String cutKey = iterator.next();
            if (cont.get(cutKey) != null && !cont.get(cutKey).equals("")) {
                if (queryStr.toString().equals("")) {
                    queryStr.append(cutKey).append("=").append(cont.get(cutKey));
                } else {
                    queryStr.append("&").append(cutKey).append("=").append(cont.get(cutKey));
                }
            }
        }
        if (ObjectUtil.isNotEmpty(key)) {
            queryStr.append("&key=" + key);
        }

        return queryStr.toString();
    }

    public static String getSignByMap(Map<String, String> map, String key) {
        StringBuffer bf = new StringBuffer();
        Map<String, String> sorteMap = new TreeMap<String, String>(map);
        for (Map.Entry<String, String> entry : sorteMap.entrySet()) {
            if (StringUtils.isBlank(entry.getValue()) || "null".equals(entry.getValue())) {
                continue;
            }
            bf.append(entry.getKey());
            bf.append("=");
            bf.append(entry.getValue());
            bf.append("&");
        }
        bf.append("key");
        bf.append("=");
        bf.append(key);
        //log.debug("sourceStr:{}",bf.toString());
        String md5 = encryptionMD5(bf.toString());
        md5 = md5.toUpperCase();

        return md5;
    }

    public static String getSignByMapObj(Map<String, Object> map, String key) {
        StringBuffer bf = new StringBuffer();
        Map<String, Object> sorteMap = new TreeMap<String, Object>(map);
        for (Map.Entry<String, Object> entry : sorteMap.entrySet()) {
            if (entry.getValue() == null) {
                continue;
            }
            if (entry.getValue() instanceof String) {
                String val = String.valueOf(entry.getValue());
                if (!org.springframework.util.StringUtils.hasText(val)) {
                    continue;
                }
            }
            bf.append(entry.getKey());
            bf.append("=");
            bf.append(entry.getValue());
            bf.append("&");
        }
        bf.append("key");
        bf.append("=");
        bf.append(key);
        String md5 = encryptionMD5(bf.toString());
        md5 = md5.toUpperCase();
        //logger.info("sourceStr:{},signVal:{}",bf.toString(),md5.toLowerCase());
        return md5;
    }

    public static void main(String[] args)  {
        String recvid = "1781054b-eb20-40a2-a6fd-d665c0638029";
        String orderid = "20211111111";
        String amount = "12.34";
        String apikey = "90d4df4b51c341c8bc2ba785d495e9c9";

        String signStr = recvid + orderid + amount + apikey;
        String sign = goMd5(signStr);

        System.out.println("Sign: " + sign); // 844c3dd058470321682106306d3deed8

        Map<String, Object> map = new HashMap<>();
        map.put("accNo", "6223540022");
        map.put("amount", "200");
        map.put("bankCode", "TH_BOT");
        map.put("country", "IN");
        map.put("merchantId", String.valueOf(1324916154027738340L));
        map.put("notifyUrl", "https://xhf-test-api-pay.imbillls.com/pay/test");
        map.put("orderId", "175686482603833201");
        map.put("payType", String.valueOf(801960));
        map.put("phone", "3123456789");
        // 注意：sign 不参与签名计算，所以不要放入 map
        String key = "FOGdkZ6TYEErpyNY5e6Gm4AtGgkWWKmhnOc2-TelQ9HFWtbXmufPSswPWjJV4Wt_djCRJkRUYiYlwg";

        String sign1 = getSignByMapObj(map, key);
        System.out.println("Sign: " + sign1 ); // DF3F8A2D31536E5D5FAB5D281D58BBB7
    }

    public static String genFixSignByMapObj(Map<String, Object> map, String key) {
        StringBuffer bf = new StringBuffer();
        Map<String, Object> sorteMap = new TreeMap<String, Object>(map);
        for (Map.Entry<String, Object> entry : sorteMap.entrySet()) {
            if (entry.getValue() == null) {
                continue;
            }
            if (entry.getValue() instanceof String) {
                String val = String.valueOf(entry.getValue());
                if (!org.springframework.util.StringUtils.hasText(val)) {
                    continue;
                }
            }
            bf.append(entry.getKey());
            bf.append("=");
            bf.append(entry.getValue());
            bf.append("&");
        }
        String md5OneVal = encryptionMD5(bf.toString());
        String md5SignVal = encryptionMD5(md5OneVal.concat(key));
        //logger.info("sourceStr:{},signVal:{}",bf.toString(),md5SignVal);
        return md5SignVal;
    }

    public static String encryptionMD5(String message) {
        String hexString = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(message.getBytes());
            hexString = byteArrayToHex(digest);
        } catch (Exception e) {
            System.out.println("encryptionMD5 " + e.getMessage());
        }
        return hexString;
    }

    public static String byteArrayToHex(byte[] byteArray) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : byteArray) {
            int value = b & 0xFF;
            if (value < 16) {
                hexString.append("0");
            }
            hexString.append(Integer.toHexString(value));
        }
        return hexString.toString();
    }

    public static String getPaSignStr(Map<String, Object> map, String privateKey) {
        List<String> keyList = new ArrayList<>(map.keySet());
        Collections.sort(keyList);
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < keyList.size(); i++) {
            String key = keyList.get(i);
            Object value = map.get(key);
            if (value != null) {
                sb.append(key + "=" + value + "&");
            }
        }
        String s = sb.substring(0, sb.length() - 1);
        return RSASignature.sign(s, privateKey);
    }

    public static boolean checkPaPaySign(Map<String, Object> map, String originSignVal, String pubKey) {
        List<String> keyList = new ArrayList<>(map.keySet());
        Collections.sort(keyList);
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < keyList.size(); i++) {
            String key = keyList.get(i);
            Object value = map.get(key);
            if (value != null) {
                sb.append(key + "=" + value + "&");
            }
        }
        String s = sb.substring(0, sb.length() - 1);
        return RSASignature.doCheck(s, originSignVal, pubKey);
    }
    public static String goMd5(String str) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = md.digest(str.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return "";
        }

    }





}
