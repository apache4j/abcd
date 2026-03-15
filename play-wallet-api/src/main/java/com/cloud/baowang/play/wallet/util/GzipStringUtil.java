package com.cloud.baowang.play.wallet.util;

import com.alibaba.fastjson.JSONObject;
import com.cloud.baowang.play.api.vo.sba.SBBaseReq;

import java.io.*;
import java.util.zip.*;

public class GzipStringUtil {

    public static final String GZIP_ENCODE_UTF_8 = "UTF-8";
    public static final String GZIP_ENCODE_ISO_8859_1 = "ISO-8859-1";

    /**
     * 将给定字符串压缩为 GZIP 压缩字符串
     * @param data 入参
     * @return 压缩后
     */
    public static String compress(String data) throws IOException {
        byte[] compressedBytes = compressToBytes(data);
        return encodeToString(compressedBytes);
    }

    /**
     * 将给定的 GZIP 压缩字符串解压缩为其原始形式
     * @param compressedData 入参
     * @return 解压后
     */
    public static String decompress(String compressedData) throws IOException {
        byte[] compressedBytes = decodeFromString(compressedData);
        return decompressToString(compressedBytes);
    }

    /**
     * 将给定字符串压缩为字节数组
     * @param data 入参
     * @return 压缩后的数组
     */
    private static byte[] compressToBytes(String data) throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             GZIPOutputStream gzipOS = new GZIPOutputStream(bos)) {
            gzipOS.write(data.getBytes());
            gzipOS.finish();
            return bos.toByteArray();
        }
    }

    /**
     * 将给定的字节数组解压缩为字符串
     * @param compressedBytes 入参
     * @return 解压缩后
     */
    private static String decompressToString(byte[] compressedBytes) throws IOException {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(compressedBytes);
             ByteArrayOutputStream bos = new ByteArrayOutputStream();
             GZIPInputStream gzipIS = new GZIPInputStream(bis)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = gzipIS.read(buffer)) != -1) {
                bos.write(buffer, 0, length);
            }
            return bos.toString();
        }
    }

    /**
     * 将字节数组编码为 Base64 字符串
     */
    private static String encodeToString(byte[] bytes) {
        return java.util.Base64.getEncoder().encodeToString(bytes);
    }

    /**
     * 将 Base64 字符串解码为字节数组
     */
    private static byte[] decodeFromString(String encodedString) {
        return java.util.Base64.getDecoder().decode(encodedString);
    }

    public static void main(String[] args) throws IOException {

//        SBPlaceBetReq req = SBPlaceBetReq.builder()
//                .action("action")
//                .actualAmount(BigDecimal.valueOf(100))
//                .awayId("awayId")
//                .build();
//        String json = JSON.toJSONString(req);


        JSONObject jsonObject = new JSONObject();

        for (int i=0;i<1000;i++){
            jsonObject.put("1"+i,"23");
        }
        String json = jsonObject.toJSONString();


        SBBaseReq base = new SBBaseReq();
        base.setMessage(json);
        base.setKey("1234");
        String originalData = "{\"key\":\"7ryqqaoq1h\",\"message\":{\"action\":\"PlaceBet\",\"operationId\":\"4338900_1_31_U\",\"userId\":\"sky001\",\"currency\":20,\"matchId\":86408031,\"homeId\":473,\"awayId\":13886,\"homeName\":\"Sporting Kansas City\",\"awayName\":\"Seattle Sounders\",\"kickOffTime\":\"2024-06-08T20:29:59.000-04:00\",\"betTime\":\"2024-06-03T23:39:16.777-04:00\",\"betAmount\":10.0,\"actualAmount\":10.0,\"sportType\":1,\"sportTypeName\":\"Soccer\",\"betType\":3,\"betTypeName\":\"Over/Under\",\"oddsType\":3,\"oddsId\":690917291,\"odds\":1.91,\"betChoice\":\"Under\",\"betChoice_en\":\"Under\",\"updateTime\":\"2024-06-03T23:39:16.777-04:00\",\"leagueId\":356,\"leagueName\":\"USA MAJOR LEAGUE SOCCER\",\"leagueName_en\":\"USA MAJOR LEAGUE SOCCER\",\"sportTypeName_en\":\"Soccer\",\"betTypeName_en\":\"Over/Under\",\"homeName_en\":\"Sporting Kansas City\",\"awayName_en\":\"Seattle Sounders\",\"IP\":\"202.178.124.126\",\"isLive\":false,\"refId\":\"4338900_15_U\",\"tsId\":\"\",\"point\":\"2.50\",\"point2\":\"\",\"betTeam\":\"a\",\"homeScore\":0,\"awayScore\":0,\"baStatus\":false,\"excluding\":\"\",\"betFrom\":\"^\",\"creditAmount\":0.0,\"debitAmount\":10.0,\"oddsInfo\":\"\",\"matchDateTime\":\"2024-06-08T20:30:00.000-04:00\",\"betRemark\":\"\",\"vendorTransId\":\"123456\"}}";
        System.err.println("压缩前字符串:"+originalData);

        String compressedData = compress(originalData);
        System.err.println("压缩后字符串:"+compressedData);

        String decompressedData = decompress(compressedData);
        System.err.println("解压后字符串:" + decompressedData);


    }
}
